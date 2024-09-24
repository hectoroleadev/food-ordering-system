package dev.hectorolea.food.ordering.system.restaurant.service.domain.ports.output.message.publisher;

import dev.hectorolea.food.ordering.system.outbox.OutboxStatus;
import dev.hectorolea.food.ordering.system.restaurant.service.domain.outbox.model.OrderOutboxMessage;
import java.util.function.BiConsumer;

public interface RestaurantApprovalResponseMessagePublisher {

  void publish(
      OrderOutboxMessage orderOutboxMessage,
      BiConsumer<OrderOutboxMessage, OutboxStatus> outboxCallback);
}
