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
import dev.hectorolea.food.ordering.system.order.service.domain.entity.Order;
import dev.hectorolea.food.ordering.system.order.service.domain.event.OrderCancelledEvent;
import dev.hectorolea.food.ordering.system.order.service.domain.event.OrderCreatedEvent;
import dev.hectorolea.food.ordering.system.order.service.domain.event.OrderPaidEvent;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class OrderMessagingDataMapper {

  public PaymentRequestAvroModel orderCreatedEventToPaymentRequestAvroModel(
      OrderCreatedEvent orderCreatedEvent) {
    Order order = orderCreatedEvent.getOrder();
    return PaymentRequestAvroModel.newBuilder()
        .setId(randomUUID().toString())
        .setSagaId("")
        .setCustomerId(order.getCustomerId().getValue().toString())
        .setOrderId(order.getId().getValue().toString())
        .setPrice(order.getPrice().getAmount())
        .setCreatedAt(orderCreatedEvent.getCreatedAt().toInstant())
        .setPaymentOrderStatus(PaymentOrderStatus.PENDING)
        .build();
  }

  public PaymentRequestAvroModel orderCancelledEventToPaymentRequestAvroModel(
      OrderCancelledEvent orderCancelledEvent) {
    Order order = orderCancelledEvent.getOrder();
    return PaymentRequestAvroModel.newBuilder()
        .setId(randomUUID().toString())
        .setSagaId("")
        .setCustomerId(order.getCustomerId().getValue().toString())
        .setOrderId(order.getId().getValue().toString())
        .setPrice(order.getPrice().getAmount())
        .setCreatedAt(orderCancelledEvent.getCreatedAt().toInstant())
        .setPaymentOrderStatus(PaymentOrderStatus.CANCELLED)
        .build();
  }

  public RestaurantApprovalRequestAvroModel orderPaidEventToRestaurantApprovalRequestAvroModel(
      OrderPaidEvent orderPaidEvent) {
    Order order = orderPaidEvent.getOrder();
    return RestaurantApprovalRequestAvroModel.newBuilder()
        .setId(randomUUID().toString())
        .setSagaId("")
        .setOrderId(order.getId().getValue().toString())
        .setRestaurantId(order.getRestaurantId().getValue().toString())
        .setOrderId(order.getId().getValue().toString())
        .setRestaurantOrderStatus(
            dev.hectorolea.food.ordering.system.kafka.order.avro.model.RestaurantOrderStatus
                .valueOf(order.getOrderStatus().name()))
        .setProducts(
            order.getItems().stream()
                .map(
                    orderItem ->
                        dev.hectorolea.food.ordering.system.kafka.order.avro.model.Product
                            .newBuilder()
                            .setId(orderItem.getProduct().getId().getValue().toString())
                            .setQuantity(orderItem.getQuantity())
                            .build())
                .collect(Collectors.toList()))
        .setPrice(order.getPrice().getAmount())
        .setCreatedAt(orderPaidEvent.getCreatedAt().toInstant())
        .setRestaurantOrderStatus(RestaurantOrderStatus.PAID)
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
}
