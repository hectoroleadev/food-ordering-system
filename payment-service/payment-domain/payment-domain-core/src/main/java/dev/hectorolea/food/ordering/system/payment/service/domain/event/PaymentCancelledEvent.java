package dev.hectorolea.food.ordering.system.payment.service.domain.event;

import dev.hectorolea.food.ordering.system.payment.service.domain.entity.Payment;
import java.time.ZonedDateTime;
import java.util.Collections;

public class PaymentCancelledEvent extends PaymentEvent {

  public PaymentCancelledEvent(Payment payment, ZonedDateTime createdAt) {
    super(payment, createdAt, Collections.emptyList());
  }
}
