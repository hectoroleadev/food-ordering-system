package dev.hectorolea.food.ordering.system.restaurant.service.domain.outbox.scheduler;

import static dev.hectorolea.food.ordering.system.domain.DomainConstants.UTC;
import static dev.hectorolea.food.ordering.system.saga.order.SagaConstants.ORDER_SAGA_NAME;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.hectorolea.food.ordering.system.domain.valueobject.OrderApprovalStatus;
import dev.hectorolea.food.ordering.system.outbox.OutboxStatus;
import dev.hectorolea.food.ordering.system.restaurant.service.domain.exception.RestaurantDomainException;
import dev.hectorolea.food.ordering.system.restaurant.service.domain.outbox.model.OrderEventPayload;
import dev.hectorolea.food.ordering.system.restaurant.service.domain.outbox.model.OrderOutboxMessage;
import dev.hectorolea.food.ordering.system.restaurant.service.domain.ports.output.repository.OrderOutboxRepository;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
public class OrderOutboxHelper {

  private final OrderOutboxRepository orderOutboxRepository;
  private final ObjectMapper objectMapper;

  public OrderOutboxHelper(OrderOutboxRepository orderOutboxRepository, ObjectMapper objectMapper) {
    this.orderOutboxRepository = orderOutboxRepository;
    this.objectMapper = objectMapper;
  }

  @Transactional(readOnly = true)
  public Optional<OrderOutboxMessage> getCompletedOrderOutboxMessageBySagaIdAndOutboxStatus(
      UUID sagaId, OutboxStatus outboxStatus) {
    return orderOutboxRepository.findByTypeAndSagaIdAndOutboxStatus(
        ORDER_SAGA_NAME, sagaId, outboxStatus);
  }

  @Transactional(readOnly = true)
  public Optional<List<OrderOutboxMessage>> getOrderOutboxMessageByOutboxStatus(
      OutboxStatus outboxStatus) {
    return orderOutboxRepository.findByTypeAndOutboxStatus(ORDER_SAGA_NAME, outboxStatus);
  }

  @Transactional
  public void deleteOrderOutboxMessageByOutboxStatus(OutboxStatus outboxStatus) {
    orderOutboxRepository.deleteByTypeAndOutboxStatus(ORDER_SAGA_NAME, outboxStatus);
  }

  @Transactional
  public void saveOrderOutboxMessage(
      OrderEventPayload orderEventPayload,
      OrderApprovalStatus approvalStatus,
      OutboxStatus outboxStatus,
      UUID sagaId) {
    save(
        OrderOutboxMessage.builder()
            .id(UUID.randomUUID())
            .sagaId(sagaId)
            .createdAt(orderEventPayload.getCreatedAt())
            .processedAt(ZonedDateTime.now(ZoneId.of(UTC)))
            .type(ORDER_SAGA_NAME)
            .payload(createPayload(orderEventPayload))
            .approvalStatus(approvalStatus)
            .outboxStatus(outboxStatus)
            .build());
  }

  @Transactional
  public void updateOutboxStatus(
      OrderOutboxMessage orderPaymentOutboxMessage, OutboxStatus outboxStatus) {
    orderPaymentOutboxMessage.setOutboxStatus(outboxStatus);
    save(orderPaymentOutboxMessage);
    log.info("Order outbox table status is updated as: {}", outboxStatus.name());
  }

  private void save(OrderOutboxMessage orderPaymentOutboxMessage) {
    OrderOutboxMessage response = orderOutboxRepository.save(orderPaymentOutboxMessage);
    if (response == null) {
      throw new RestaurantDomainException("Could not save OrderOutboxMessage!");
    }
    log.info("OrderOutboxMessage saved with id: {}", orderPaymentOutboxMessage.getId());
  }

  private String createPayload(OrderEventPayload orderEventPayload) {
    try {
      return objectMapper.writeValueAsString(orderEventPayload);
    } catch (JsonProcessingException e) {
      log.error("Could not create OrderEventPayload json!", e);
      throw new RestaurantDomainException("Could not create OrderEventPayload json!", e);
    }
  }
}
