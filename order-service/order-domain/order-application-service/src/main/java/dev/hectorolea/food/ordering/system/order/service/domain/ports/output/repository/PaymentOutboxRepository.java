package dev.hectorolea.food.ordering.system.order.service.domain.ports.output.repository;

import dev.hectorolea.food.ordering.system.order.service.domain.outbox.model.payment.OrderPaymentOutboxMessage;
import dev.hectorolea.food.ordering.system.outbox.OutboxStatus;
import dev.hectorolea.food.ordering.system.saga.SagaStatus;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PaymentOutboxRepository {

  OrderPaymentOutboxMessage save(OrderPaymentOutboxMessage orderPaymentOutboxMessage);

  Optional<List<OrderPaymentOutboxMessage>> findByTypeAndOutboxStatusAndSagaStatus(
      String type, OutboxStatus outboxStatus, SagaStatus... sagaStatus);

  Optional<OrderPaymentOutboxMessage> findByTypeAndSagaIdAndSagaStatus(
      String type, UUID sagaId, SagaStatus... sagaStatus);

  void deleteByTypeAndOutboxStatusAndSagaStatus(
      String type, OutboxStatus outboxStatus, SagaStatus... sagaStatus);
}
