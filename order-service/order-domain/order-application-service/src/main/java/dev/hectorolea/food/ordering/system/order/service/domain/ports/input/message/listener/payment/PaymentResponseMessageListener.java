package dev.hectorolea.food.ordering.system.order.service.domain.ports.input.message.listener.payment;

import dev.hectorolea.food.ordering.system.order.service.domain.dto.message.PaymentResponse;

public interface PaymentResponseMessageListener {

  void paymentCompleted(PaymentResponse paymentResponse);

  void paymentCancelled(PaymentResponse paymentResponse);
}
