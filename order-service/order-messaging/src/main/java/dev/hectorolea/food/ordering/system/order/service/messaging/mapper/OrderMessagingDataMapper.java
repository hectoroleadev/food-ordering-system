package dev.hectorolea.food.ordering.system.order.service.messaging.mapper;

import static java.util.UUID.randomUUID;

import dev.hectorolea.food.ordering.system.kafka.order.avro.model.PaymentOrderStatus;
import dev.hectorolea.food.ordering.system.kafka.order.avro.model.PaymentRequestAvroModel;
import dev.hectorolea.food.ordering.system.kafka.order.avro.model.PaymentResponseAvroModel;
import dev.hectorolea.food.ordering.system.kafka.order.avro.model.RestaurantApprovalRequestAvroModel;
import dev.hectorolea.food.ordering.system.kafka.order.avro.model.RestaurantApprovalResponseAvroModel;
import dev.hectorolea.food.ordering.system.kafka.order.avro.model.RestaurantOrderStatus;
import dev.hectorolea.food.ordering.system.order.service.domain.dto.message.PaymentResponse;
import dev.hectorolea.food.ordering.system.order.service.domain.dto.message.RestaurantApprovalResponse;
import dev.hectorolea.food.ordering.system.order.service.domain.outbox.model.approval.OrderApprovalEventPayload;
import dev.hectorolea.food.ordering.system.order.service.domain.outbox.model.payment.OrderPaymentEventPayload;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class OrderMessagingDataMapper {

  public PaymentRequestAvroModel orderPaymentEventToPaymentRequestAvroModel(
      String sagaId, OrderPaymentEventPayload orderPaymentEventPayload) {
    return PaymentRequestAvroModel.newBuilder()
        .setId(randomUUID().toString())
        .setSagaId(sagaId)
        .setCustomerId(orderPaymentEventPayload.getCustomerId())
        .setOrderId(orderPaymentEventPayload.getOrderId())
        .setPrice(orderPaymentEventPayload.getPrice())
        .setCreatedAt(orderPaymentEventPayload.getCreatedAt().toInstant())
        .setPaymentOrderStatus(
            PaymentOrderStatus.valueOf(orderPaymentEventPayload.getPaymentOrderStatus()))
        .build();
  }

  public PaymentResponse paymentResponseAvroModelToPaymentResponse(
      PaymentResponseAvroModel paymentResponseAvroModel) {
    return PaymentResponse.builder()
        .id(paymentResponseAvroModel.getId())
        .sagaId(paymentResponseAvroModel.getSagaId())
        .paymentId(paymentResponseAvroModel.getPaymentId())
        .customerId(paymentResponseAvroModel.getCustomerId())
        .orderId(paymentResponseAvroModel.getOrderId())
        .price(paymentResponseAvroModel.getPrice())
        .createdAt(paymentResponseAvroModel.getCreatedAt())
        .paymentStatus(
            dev.hectorolea.food.ordering.system.domain.valueobject.PaymentStatus.valueOf(
                paymentResponseAvroModel.getPaymentStatus().name()))
        .failureMessages(paymentResponseAvroModel.getFailureMessages())
        .build();
  }

  public RestaurantApprovalResponse approvalResponseAvroModelToApprovalResponse(
      RestaurantApprovalResponseAvroModel restaurantApprovalResponseAvroModel) {
    return RestaurantApprovalResponse.builder()
        .id(restaurantApprovalResponseAvroModel.getId())
        .sagaId(restaurantApprovalResponseAvroModel.getSagaId())
        .restaurantId(restaurantApprovalResponseAvroModel.getRestaurantId())
        .orderId(restaurantApprovalResponseAvroModel.getOrderId())
        .createdAt(restaurantApprovalResponseAvroModel.getCreatedAt())
        .orderApprovalStatus(
            dev.hectorolea.food.ordering.system.domain.valueobject.OrderApprovalStatus.valueOf(
                restaurantApprovalResponseAvroModel.getOrderApprovalStatus().name()))
        .failureMessages(restaurantApprovalResponseAvroModel.getFailureMessages())
        .build();
  }

  public RestaurantApprovalRequestAvroModel orderApprovalEventToRestaurantApprovalRequestAvroModel(
      String sagaId, OrderApprovalEventPayload orderApprovalEventPayload) {
    return RestaurantApprovalRequestAvroModel.newBuilder()
        .setId(randomUUID().toString())
        .setSagaId(sagaId)
        .setOrderId(orderApprovalEventPayload.getOrderId())
        .setRestaurantId(orderApprovalEventPayload.getRestaurantId())
        .setRestaurantOrderStatus(
            RestaurantOrderStatus.valueOf(orderApprovalEventPayload.getRestaurantOrderStatus()))
        .setProducts(
            orderApprovalEventPayload.getProducts().stream()
                .map(
                    orderApprovalEventProduct ->
                        dev.hectorolea.food.ordering.system.kafka.order.avro.model.Product
                            .newBuilder()
                            .setId(orderApprovalEventProduct.getId())
                            .setQuantity(orderApprovalEventProduct.getQuantity())
                            .build())
                .collect(Collectors.toList()))
        .setPrice(orderApprovalEventPayload.getPrice())
        .setCreatedAt(orderApprovalEventPayload.getCreatedAt().toInstant())
        .build();
  }
}
