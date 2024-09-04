package dev.hectorolea.food.ordering.system.order.service.domain.ports.output.message.publisher.restaurantapproval;

import dev.hectorolea.food.ordering.system.domain.event.publisher.DomainEventPublisher;
import dev.hectorolea.food.ordering.system.order.service.domain.event.OrderPaidEvent;

public interface OrderPaidRestaurantRequestMessagePublisher
    extends DomainEventPublisher<OrderPaidEvent> {}
