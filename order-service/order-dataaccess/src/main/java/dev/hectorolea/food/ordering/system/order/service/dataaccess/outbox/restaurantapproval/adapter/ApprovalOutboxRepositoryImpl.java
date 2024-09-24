package dev.hectorolea.food.ordering.system.order.service.dataaccess.outbox.restaurantapproval.adapter;

import static java.util.Arrays.asList;

import dev.hectorolea.food.ordering.system.order.service.dataaccess.outbox.restaurantapproval.exception.ApprovalOutboxNotFoundException;
import dev.hectorolea.food.ordering.system.order.service.dataaccess.outbox.restaurantapproval.mapper.ApprovalOutboxDataAccessMapper;
import dev.hectorolea.food.ordering.system.order.service.dataaccess.outbox.restaurantapproval.repository.ApprovalOutboxJpaRepository;
import dev.hectorolea.food.ordering.system.order.service.domain.outbox.model.approval.OrderApprovalOutboxMessage;
import dev.hectorolea.food.ordering.system.order.service.domain.ports.output.repository.ApprovalOutboxRepository;
import dev.hectorolea.food.ordering.system.outbox.OutboxStatus;
import dev.hectorolea.food.ordering.system.saga.SagaStatus;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class ApprovalOutboxRepositoryImpl implements ApprovalOutboxRepository {

  private final ApprovalOutboxJpaRepository approvalOutboxJpaRepository;
  private final ApprovalOutboxDataAccessMapper approvalOutboxDataAccessMapper;

  public ApprovalOutboxRepositoryImpl(
      ApprovalOutboxJpaRepository approvalOutboxJpaRepository,
      ApprovalOutboxDataAccessMapper approvalOutboxDataAccessMapper) {
    this.approvalOutboxJpaRepository = approvalOutboxJpaRepository;
    this.approvalOutboxDataAccessMapper = approvalOutboxDataAccessMapper;
  }

  @Override
  public OrderApprovalOutboxMessage save(OrderApprovalOutboxMessage orderApprovalOutboxMessage) {
    return approvalOutboxDataAccessMapper.approvalOutboxEntityToOrderApprovalOutboxMessage(
        approvalOutboxJpaRepository.save(
            approvalOutboxDataAccessMapper.orderCreatedOutboxMessageToOutboxEntity(
                orderApprovalOutboxMessage)));
  }

  @Override
  public Optional<List<OrderApprovalOutboxMessage>> findByTypeAndOutboxStatusAndSagaStatus(
      String sagaType, OutboxStatus outboxStatus, SagaStatus... sagaStatus) {
    return Optional.of(
        approvalOutboxJpaRepository
            .findByTypeAndOutboxStatusAndSagaStatusIn(sagaType, outboxStatus, asList(sagaStatus))
            .orElseThrow(
                () ->
                    new ApprovalOutboxNotFoundException(
                        "Approval outbox object " + "could be found for saga type " + sagaType))
            .stream()
            .map(approvalOutboxDataAccessMapper::approvalOutboxEntityToOrderApprovalOutboxMessage)
            .collect(Collectors.toList()));
  }

  @Override
  public Optional<OrderApprovalOutboxMessage> findByTypeAndSagaIdAndSagaStatus(
      String type, UUID sagaId, SagaStatus... sagaStatus) {
    return approvalOutboxJpaRepository
        .findByTypeAndSagaIdAndSagaStatusIn(type, sagaId, asList(sagaStatus))
        .map(approvalOutboxDataAccessMapper::approvalOutboxEntityToOrderApprovalOutboxMessage);
  }

  @Override
  public void deleteByTypeAndOutboxStatusAndSagaStatus(
      String type, OutboxStatus outboxStatus, SagaStatus... sagaStatus) {
    approvalOutboxJpaRepository.deleteByTypeAndOutboxStatusAndSagaStatusIn(
        type, outboxStatus, asList(sagaStatus));
  }
}
