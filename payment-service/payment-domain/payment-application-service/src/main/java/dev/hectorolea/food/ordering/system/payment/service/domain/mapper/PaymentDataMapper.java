package dev.hectorolea.food.ordering.system.payment.service.domain.mapper;

import static java.util.UUID.fromString;

import dev.hectorolea.food.ordering.system.domain.valueobject.CustomerId;
import dev.hectorolea.food.ordering.system.domain.valueobject.Money;
import dev.hectorolea.food.ordering.system.domain.valueobject.OrderId;
import dev.hectorolea.food.ordering.system.payment.service.domain.dto.PaymentRequest;
import dev.hectorolea.food.ordering.system.payment.service.domain.entity.Payment;
import dev.hectorolea.food.ordering.system.payment.service.domain.event.PaymentEvent;
import dev.hectorolea.food.ordering.system.payment.service.domain.outbox.model.OrderEventPayload;
import org.springframework.stereotype.Component;

@Component
public class PaymentDataMapper {

  public Payment paymentRequestModelToPayment(PaymentRequest paymentRequest) {
    return Payment.builder()
        .orderId(new OrderId(fromString(paymentRequest.getOrderId())))
        .customerId(new CustomerId(fromString(paymentRequest.getCustomerId())))
        .price(new Money(paymentRequest.getPrice()))
        .build();
  }

  public OrderEventPayload paymentEventToOrderEventPayload(PaymentEvent paymentEvent) {
    return OrderEventPayload.builder()
        .paymentId(paymentEvent.getPayment().getId().getValue().toString())
        .customerId(paymentEvent.getPayment().getCustomerId().getValue().toString())
        .orderId(paymentEvent.getPayment().getOrderId().getValue().toString())
        .price(paymentEvent.getPayment().getPrice().getAmount())
        .createdAt(paymentEvent.getCreatedAt())
        .paymentStatus(paymentEvent.getPayment().getPaymentStatus().name())
        .failureMessages(paymentEvent.getFailureMessages())
        .build();
  }
}
