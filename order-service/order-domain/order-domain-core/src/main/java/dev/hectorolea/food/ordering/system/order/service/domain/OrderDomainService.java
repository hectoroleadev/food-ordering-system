package dev.hectorolea.food.ordering.system.order.service.domain;

import dev.hectorolea.food.ordering.system.domain.event.publisher.DomainEventPublisher;
import dev.hectorolea.food.ordering.system.order.service.domain.entity.Order;
import dev.hectorolea.food.ordering.system.order.service.domain.entity.Restaurant;
import dev.hectorolea.food.ordering.system.order.service.domain.event.OrderCancelledEvent;
import dev.hectorolea.food.ordering.system.order.service.domain.event.OrderCreatedEvent;
import dev.hectorolea.food.ordering.system.order.service.domain.event.OrderPaidEvent;
import java.util.List;

public interface OrderDomainService {

  OrderCreatedEvent validateAndInitiateOrder(
      Order order,
      Restaurant restaurant,
      DomainEventPublisher<OrderCreatedEvent> orderCreatedEventDomainEventPublisher);

  OrderPaidEvent payOrder(
      Order order, DomainEventPublisher<OrderPaidEvent> orderPaidEventDomainEventPublisher);

  void approveOrder(Order order);

  OrderCancelledEvent cancelOrderPayment(
      Order order,
      List<String> failureMessages,
      DomainEventPublisher<OrderCancelledEvent> orderCancelledEventDomainEventPublisher);

  void cancelOrder(Order order, List<String> failureMessages);
}
