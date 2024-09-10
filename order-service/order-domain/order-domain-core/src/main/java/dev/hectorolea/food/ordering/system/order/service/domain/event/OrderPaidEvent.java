package dev.hectorolea.food.ordering.system.order.service.domain.event;

import dev.hectorolea.food.ordering.system.domain.event.publisher.DomainEventPublisher;
import dev.hectorolea.food.ordering.system.order.service.domain.entity.Order;
import java.time.ZonedDateTime;

public class OrderPaidEvent extends OrderEvent {
  private final DomainEventPublisher<OrderPaidEvent> orderPaidEventDomainEventPublisher;

  public OrderPaidEvent(
      Order order,
      ZonedDateTime createdAt,
      DomainEventPublisher<OrderPaidEvent> orderPaidEventDomainEventPublisher) {
    super(order, createdAt);
    this.orderPaidEventDomainEventPublisher = orderPaidEventDomainEventPublisher;
  }

  @Override
  public void fire() {
    orderPaidEventDomainEventPublisher.publish(this);
  }
}
