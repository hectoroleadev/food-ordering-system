package dev.hectorolea.food.ordering.system.order.service.domain.event;

import dev.hectorolea.food.ordering.system.order.service.domain.entity.Order;
import java.time.ZonedDateTime;

public class OrderCreatedEvent extends OrderEvent {

  public OrderCreatedEvent(Order order, ZonedDateTime createdAt) {
    super(order, createdAt);
  }
}
