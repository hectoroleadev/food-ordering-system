package dev.hectorolea.food.ordering.system.payment.service.domain.event;

import static java.util.Collections.emptyList;

import dev.hectorolea.food.ordering.system.payment.service.domain.entity.Payment;
import java.time.ZonedDateTime;

public class PaymentCompletedEvent extends PaymentEvent {

  public PaymentCompletedEvent(Payment payment, ZonedDateTime createdAt) {
    super(payment, createdAt, emptyList());
  }
}
