package dev.hectorolea.food.ordering.system.order.service.domain;

import static dev.hectorolea.food.ordering.system.domain.DomainConstants.UTC;
import static java.time.ZoneId.of;
import static java.time.ZonedDateTime.now;
import static java.util.UUID.fromString;

import dev.hectorolea.food.ordering.system.domain.valueobject.OrderId;
import dev.hectorolea.food.ordering.system.domain.valueobject.OrderStatus;
import dev.hectorolea.food.ordering.system.domain.valueobject.PaymentStatus;
import dev.hectorolea.food.ordering.system.order.service.domain.dto.message.PaymentResponse;
import dev.hectorolea.food.ordering.system.order.service.domain.entity.Order;
import dev.hectorolea.food.ordering.system.order.service.domain.event.OrderPaidEvent;
import dev.hectorolea.food.ordering.system.order.service.domain.exception.OrderDomainException;
import dev.hectorolea.food.ordering.system.order.service.domain.exception.OrderNotFoundException;
import dev.hectorolea.food.ordering.system.order.service.domain.mapper.OrderDataMapper;
import dev.hectorolea.food.ordering.system.order.service.domain.outbox.model.approval.OrderApprovalOutboxMessage;
import dev.hectorolea.food.ordering.system.order.service.domain.outbox.model.payment.OrderPaymentOutboxMessage;
import dev.hectorolea.food.ordering.system.order.service.domain.outbox.scheduler.approval.ApprovalOutboxHelper;
import dev.hectorolea.food.ordering.system.order.service.domain.outbox.scheduler.payment.PaymentOutboxHelper;
import dev.hectorolea.food.ordering.system.order.service.domain.ports.output.repository.OrderRepository;
import dev.hectorolea.food.ordering.system.outbox.OutboxStatus;
import dev.hectorolea.food.ordering.system.saga.SagaStatus;
import dev.hectorolea.food.ordering.system.saga.SagaStep;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
public class OrderPaymentSaga implements SagaStep<PaymentResponse> {

  private final OrderDomainService orderDomainService;
  private final OrderSagaHelper orderSagaHelper;
  private final PaymentOutboxHelper paymentOutboxHelper;
  private final ApprovalOutboxHelper approvalOutboxHelper;
  private final OrderDataMapper orderDataMapper;
  private final OrderRepository orderRepository;

  public OrderPaymentSaga(
      OrderDomainService orderDomainService,
      OrderSagaHelper orderSagaHelper,
      PaymentOutboxHelper paymentOutboxHelper,
      ApprovalOutboxHelper approvalOutboxHelper,
      OrderDataMapper orderDataMapper,
      OrderRepository orderRepository) {
    this.orderDomainService = orderDomainService;
    this.orderSagaHelper = orderSagaHelper;
    this.paymentOutboxHelper = paymentOutboxHelper;
    this.approvalOutboxHelper = approvalOutboxHelper;
    this.orderDataMapper = orderDataMapper;
    this.orderRepository = orderRepository;
  }

  @Override
  @Transactional
  public void process(PaymentResponse paymentResponse) {
    Optional<OrderPaymentOutboxMessage> orderPaymentOutboxMessageResponse =
        paymentOutboxHelper.getPaymentOutboxMessageBySagaIdAndSagaStatus(
            fromString(paymentResponse.getSagaId()), SagaStatus.STARTED);

    if (orderPaymentOutboxMessageResponse.isEmpty()) {
      log.info(
          "An outbox message with saga id: {} is already processed!", paymentResponse.getSagaId());
      return;
    }

    OrderPaymentOutboxMessage orderPaymentOutboxMessage = orderPaymentOutboxMessageResponse.get();

    OrderPaidEvent domainEvent = completePaymentForOrder(paymentResponse);

    SagaStatus sagaStatus =
        orderSagaHelper.orderStatusToSagaStatus(domainEvent.getOrder().getOrderStatus());

    paymentOutboxHelper.save(
        getUpdatedPaymentOutboxMessage(
            orderPaymentOutboxMessage, domainEvent.getOrder().getOrderStatus(), sagaStatus));

    approvalOutboxHelper.saveApprovalOutboxMessage(
        orderDataMapper.orderPaidEventToOrderApprovalEventPayload(domainEvent),
        domainEvent.getOrder().getOrderStatus(),
        sagaStatus,
        OutboxStatus.STARTED,
        fromString(paymentResponse.getSagaId()));

    log.info("Order with id: {} is paid", domainEvent.getOrder().getId().getValue());
  }

  @Override
  @Transactional
  public void rollback(PaymentResponse paymentResponse) {
    Optional<OrderPaymentOutboxMessage> orderPaymentOutboxMessageResponse =
        paymentOutboxHelper.getPaymentOutboxMessageBySagaIdAndSagaStatus(
            fromString(paymentResponse.getSagaId()),
            getCurrentSagaStatus(paymentResponse.getPaymentStatus()));

    if (orderPaymentOutboxMessageResponse.isEmpty()) {
      log.info(
          "An outbox message with saga id: {} is already roll backed!",
          paymentResponse.getSagaId());
      return;
    }

    OrderPaymentOutboxMessage orderPaymentOutboxMessage = orderPaymentOutboxMessageResponse.get();

    Order order = rollbackPaymentForOrder(paymentResponse);

    SagaStatus sagaStatus = orderSagaHelper.orderStatusToSagaStatus(order.getOrderStatus());

    paymentOutboxHelper.save(
        getUpdatedPaymentOutboxMessage(
            orderPaymentOutboxMessage, order.getOrderStatus(), sagaStatus));

    if (paymentResponse.getPaymentStatus() == PaymentStatus.CANCELLED) {
      approvalOutboxHelper.save(
          getUpdatedApprovalOutboxMessage(
              paymentResponse.getSagaId(), order.getOrderStatus(), sagaStatus));
    }

    log.info("Order with id: {} is cancelled", order.getId().getValue());
  }

  private OrderPaidEvent completePaymentForOrder(PaymentResponse paymentResponse) {
    log.info("Completing payment for order with id: {}", paymentResponse.getOrderId());
    Order order = findOrder(paymentResponse.getOrderId());
    OrderPaidEvent domainEvent = orderDomainService.payOrder(order);
    orderRepository.save(order);
    return domainEvent;
  }

  private Order findOrder(String orderId) {
    Optional<Order> orderResponse = orderRepository.findById(new OrderId(fromString(orderId)));
    if (orderResponse.isEmpty()) {
      log.error("Order with id: {} could not be found!", orderId);
      throw new OrderNotFoundException("Order with id " + orderId + " could not be found!");
    }
    return orderResponse.get();
  }

  private OrderPaymentOutboxMessage getUpdatedPaymentOutboxMessage(
      OrderPaymentOutboxMessage orderPaymentOutboxMessage,
      OrderStatus orderStatus,
      SagaStatus sagaStatus) {
    orderPaymentOutboxMessage.setProcessedAt(now(of(UTC)));
    orderPaymentOutboxMessage.setOrderStatus(orderStatus);
    orderPaymentOutboxMessage.setSagaStatus(sagaStatus);
    return orderPaymentOutboxMessage;
  }

  private SagaStatus[] getCurrentSagaStatus(PaymentStatus paymentStatus) {
    return switch (paymentStatus) {
      case COMPLETED -> new SagaStatus[] {SagaStatus.STARTED};
      case CANCELLED -> new SagaStatus[] {SagaStatus.PROCESSING};
      case FAILED -> new SagaStatus[] {SagaStatus.STARTED, SagaStatus.PROCESSING};
    };
  }

  private Order rollbackPaymentForOrder(PaymentResponse paymentResponse) {
    log.info("Cancelling order with id: {}", paymentResponse.getOrderId());
    Order order = findOrder(paymentResponse.getOrderId());
    orderDomainService.cancelOrder(order, paymentResponse.getFailureMessages());
    orderRepository.save(order);
    return order;
  }

  private OrderApprovalOutboxMessage getUpdatedApprovalOutboxMessage(
      String sagaId, OrderStatus orderStatus, SagaStatus sagaStatus) {
    Optional<OrderApprovalOutboxMessage> orderApprovalOutboxMessageResponse =
        approvalOutboxHelper.getApprovalOutboxMessageBySagaIdAndSagaStatus(
            fromString(sagaId), SagaStatus.COMPENSATING);
    if (orderApprovalOutboxMessageResponse.isEmpty()) {
      throw new OrderDomainException(
          "Approval outbox message could not be found in "
              + SagaStatus.COMPENSATING.name()
              + " status!");
    }
    OrderApprovalOutboxMessage orderApprovalOutboxMessage =
        orderApprovalOutboxMessageResponse.get();
    orderApprovalOutboxMessage.setProcessedAt(now(of(UTC)));
    orderApprovalOutboxMessage.setOrderStatus(orderStatus);
    orderApprovalOutboxMessage.setSagaStatus(sagaStatus);
    return orderApprovalOutboxMessage;
  }
}
