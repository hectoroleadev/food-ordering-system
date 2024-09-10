package dev.hectorolea.food.ordering.system.payment.service.domain.ports.output.message.publisher;

import dev.hectorolea.food.ordering.system.domain.event.publisher.DomainEventPublisher;
import dev.hectorolea.food.ordering.system.payment.service.domain.event.PaymentCompletedEvent;

public interface PaymentCompletedMessagePublisher
    extends DomainEventPublisher<PaymentCompletedEvent> {}
