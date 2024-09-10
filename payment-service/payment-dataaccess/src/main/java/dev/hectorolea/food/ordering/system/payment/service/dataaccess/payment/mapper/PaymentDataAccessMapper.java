package dev.hectorolea.food.ordering.system.payment.service.dataaccess.payment.mapper;

import dev.hectorolea.food.ordering.system.domain.valueobject.CustomerId;
import dev.hectorolea.food.ordering.system.domain.valueobject.Money;
import dev.hectorolea.food.ordering.system.domain.valueobject.OrderId;
import dev.hectorolea.food.ordering.system.payment.service.dataaccess.payment.entity.PaymentEntity;
import dev.hectorolea.food.ordering.system.payment.service.domain.entity.Payment;
import dev.hectorolea.food.ordering.system.payment.service.domain.valueobject.PaymentId;
import org.springframework.stereotype.Component;

@Component
public class PaymentDataAccessMapper {

  public PaymentEntity paymentToPaymentEntity(Payment payment) {
    return PaymentEntity.builder()
        .id(payment.getId().getValue())
        .customerId(payment.getCustomerId().getValue())
        .orderId(payment.getOrderId().getValue())
        .price(payment.getPrice().getAmount())
        .status(payment.getPaymentStatus())
        .createdAt(payment.getCreatedAt())
        .build();
  }

  public Payment paymentEntityToPayment(PaymentEntity paymentEntity) {
    return Payment.builder()
        .paymentId(new PaymentId(paymentEntity.getId()))
        .customerId(new CustomerId(paymentEntity.getCustomerId()))
        .orderId(new OrderId(paymentEntity.getOrderId()))
        .price(new Money(paymentEntity.getPrice()))
        .createdAt(paymentEntity.getCreatedAt())
        .build();
  }
}
