package dev.hectorolea.food.ordering.system.order.service.domain.outbox.scheduler.approval;

import static dev.hectorolea.food.ordering.system.saga.order.SagaConstants.ORDER_SAGA_NAME;
import static java.util.Objects.isNull;
import static java.util.UUID.randomUUID;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.hectorolea.food.ordering.system.domain.valueobject.OrderStatus;
import dev.hectorolea.food.ordering.system.order.service.domain.exception.OrderDomainException;
import dev.hectorolea.food.ordering.system.order.service.domain.outbox.model.approval.OrderApprovalEventPayload;
import dev.hectorolea.food.ordering.system.order.service.domain.outbox.model.approval.OrderApprovalOutboxMessage;
import dev.hectorolea.food.ordering.system.order.service.domain.ports.output.repository.ApprovalOutboxRepository;
import dev.hectorolea.food.ordering.system.outbox.OutboxStatus;
import dev.hectorolea.food.ordering.system.saga.SagaStatus;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
public class ApprovalOutboxHelper {

  private final ApprovalOutboxRepository approvalOutboxRepository;
  private final ObjectMapper objectMapper;

  public ApprovalOutboxHelper(
      ApprovalOutboxRepository approvalOutboxRepository, ObjectMapper objectMapper) {
    this.approvalOutboxRepository = approvalOutboxRepository;
    this.objectMapper = objectMapper;
  }

  @Transactional(readOnly = true)
  public Optional<List<OrderApprovalOutboxMessage>>
      getApprovalOutboxMessageByOutboxStatusAndSagaStatus(
          OutboxStatus outboxStatus, SagaStatus... sagaStatus) {
    return approvalOutboxRepository.findByTypeAndOutboxStatusAndSagaStatus(
        ORDER_SAGA_NAME, outboxStatus, sagaStatus);
  }

  @Transactional(readOnly = true)
  public Optional<OrderApprovalOutboxMessage> getApprovalOutboxMessageBySagaIdAndSagaStatus(
      UUID sagaId, SagaStatus... sagaStatus) {
    return approvalOutboxRepository.findByTypeAndSagaIdAndSagaStatus(
        ORDER_SAGA_NAME, sagaId, sagaStatus);
  }

  @Transactional
  public void save(OrderApprovalOutboxMessage orderApprovalOutboxMessage) {
    OrderApprovalOutboxMessage response = approvalOutboxRepository.save(orderApprovalOutboxMessage);
    if (isNull(response )) {
      log.error(
          "Could not save OrderApprovalOutboxMessage with outbox id: {}",
          orderApprovalOutboxMessage.getId());
      throw new OrderDomainException(
          "Could not save OrderApprovalOutboxMessage with outbox id: "
              + orderApprovalOutboxMessage.getId());
    }
    log.info(
        "OrderApprovalOutboxMessage saved with outbox id: {}", orderApprovalOutboxMessage.getId());
  }

  @Transactional
  public void saveApprovalOutboxMessage(
      OrderApprovalEventPayload orderApprovalEventPayload,
      OrderStatus orderStatus,
      SagaStatus sagaStatus,
      OutboxStatus outboxStatus,
      UUID sagaId) {
    save(
        OrderApprovalOutboxMessage.builder()
            .id(randomUUID())
            .sagaId(sagaId)
            .createdAt(orderApprovalEventPayload.getCreatedAt())
            .type(ORDER_SAGA_NAME)
            .payload(createPayload(orderApprovalEventPayload))
            .orderStatus(orderStatus)
            .sagaStatus(sagaStatus)
            .outboxStatus(outboxStatus)
            .build());
  }

  @Transactional
  public void deleteApprovalOutboxMessageByOutboxStatusAndSagaStatus(
      OutboxStatus outboxStatus, SagaStatus... sagaStatus) {
    approvalOutboxRepository.deleteByTypeAndOutboxStatusAndSagaStatus(
        ORDER_SAGA_NAME, outboxStatus, sagaStatus);
  }

  private String createPayload(OrderApprovalEventPayload orderApprovalEventPayload) {
    try {
      return objectMapper.writeValueAsString(orderApprovalEventPayload);
    } catch (JsonProcessingException e) {
      log.error(
          "Could not create OrderApprovalEventPayload for order id: {}",
          orderApprovalEventPayload.getOrderId(),
          e);
      throw new OrderDomainException(
          "Could not create OrderApprovalEventPayload for order id: "
              + orderApprovalEventPayload.getOrderId(),
          e);
    }
  }
}
