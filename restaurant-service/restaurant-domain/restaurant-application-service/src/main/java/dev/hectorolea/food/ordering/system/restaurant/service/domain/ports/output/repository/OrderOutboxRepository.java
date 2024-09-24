package dev.hectorolea.food.ordering.system.restaurant.service.domain.ports.output.repository;

import dev.hectorolea.food.ordering.system.outbox.OutboxStatus;
import dev.hectorolea.food.ordering.system.restaurant.service.domain.outbox.model.OrderOutboxMessage;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderOutboxRepository {

  OrderOutboxMessage save(OrderOutboxMessage orderOutboxMessage);

  Optional<List<OrderOutboxMessage>> findByTypeAndOutboxStatus(
      String type, OutboxStatus outboxStatus);

  Optional<OrderOutboxMessage> findByTypeAndSagaIdAndOutboxStatus(
      String type, UUID sagaId, OutboxStatus outboxStatus);

  void deleteByTypeAndOutboxStatus(String type, OutboxStatus outboxStatus);
}
