package dev.hectorolea.food.ordering.system.payment.service.domain.event;

import static java.util.Collections.emptyList;

import dev.hectorolea.food.ordering.system.domain.event.publisher.DomainEventPublisher;
import dev.hectorolea.food.ordering.system.payment.service.domain.entity.Payment;
import java.time.ZonedDateTime;

public class PaymentCompletedEvent extends PaymentEvent {

  private final DomainEventPublisher<PaymentCompletedEvent>
      paymentCompletedEventDomainEventPublisher;

  public PaymentCompletedEvent(
      Payment payment,
      ZonedDateTime createdAt,
      DomainEventPublisher<PaymentCompletedEvent> paymentCompletedEventDomainEventPublisher) {
    super(payment, createdAt, emptyList());
    this.paymentCompletedEventDomainEventPublisher = paymentCompletedEventDomainEventPublisher;
  }

  @Override
  public void fire() {
    paymentCompletedEventDomainEventPublisher.publish(this);
  }
}
