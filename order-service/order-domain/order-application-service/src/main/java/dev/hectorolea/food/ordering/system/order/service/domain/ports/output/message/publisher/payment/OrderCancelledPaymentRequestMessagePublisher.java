package dev.hectorolea.food.ordering.system.order.service.domain.ports.output.message.publisher.payment;

import dev.hectorolea.food.ordering.system.domain.event.publisher.DomainEventPublisher;
import dev.hectorolea.food.ordering.system.order.service.domain.event.OrderCancelledEvent;

public interface OrderCancelledPaymentRequestMessagePublisher
    extends DomainEventPublisher<OrderCancelledEvent> {}
