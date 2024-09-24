package dev.hectorolea.food.ordering.system.payment.service.domain.messaging.mapper;

import static java.util.UUID.randomUUID;

import dev.hectorolea.food.ordering.system.domain.valueobject.PaymentOrderStatus;
import dev.hectorolea.food.ordering.system.kafka.order.avro.model.PaymentRequestAvroModel;
import dev.hectorolea.food.ordering.system.kafka.order.avro.model.PaymentResponseAvroModel;
import dev.hectorolea.food.ordering.system.kafka.order.avro.model.PaymentStatus;
import dev.hectorolea.food.ordering.system.payment.service.domain.dto.PaymentRequest;
import dev.hectorolea.food.ordering.system.payment.service.domain.outbox.model.OrderEventPayload;
import org.springframework.stereotype.Component;

@Component
public class PaymentMessagingDataMapper {

  public PaymentRequest paymentRequestAvroModelToPaymentRequest(
      PaymentRequestAvroModel paymentRequestAvroModel) {
    return PaymentRequest.builder()
        .id(paymentRequestAvroModel.getId())
        .sagaId(paymentRequestAvroModel.getSagaId())
        .customerId(paymentRequestAvroModel.getCustomerId())
        .orderId(paymentRequestAvroModel.getOrderId())
        .price(paymentRequestAvroModel.getPrice())
        .createdAt(paymentRequestAvroModel.getCreatedAt())
        .paymentOrderStatus(
            PaymentOrderStatus.valueOf(paymentRequestAvroModel.getPaymentOrderStatus().name()))
        .build();
  }

  public PaymentResponseAvroModel orderEventPayloadToPaymentResponseAvroModel(
      String sagaId, OrderEventPayload orderEventPayload) {
    return PaymentResponseAvroModel.newBuilder()
        .setId(randomUUID().toString())
        .setSagaId(sagaId)
        .setPaymentId(orderEventPayload.getPaymentId())
        .setCustomerId(orderEventPayload.getCustomerId())
        .setOrderId(orderEventPayload.getOrderId())
        .setPrice(orderEventPayload.getPrice())
        .setCreatedAt(orderEventPayload.getCreatedAt().toInstant()) // ??
        .setPaymentStatus(PaymentStatus.valueOf(orderEventPayload.getPaymentStatus()))
        .setFailureMessages(orderEventPayload.getFailureMessages())
        .build();
  }
}
