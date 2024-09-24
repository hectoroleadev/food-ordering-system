package dev.hectorolea.food.ordering.system.restaurant.service.messaging.mapper;

import static java.util.UUID.randomUUID;

import dev.hectorolea.food.ordering.system.domain.valueobject.ProductId;
import dev.hectorolea.food.ordering.system.domain.valueobject.RestaurantOrderStatus;
import dev.hectorolea.food.ordering.system.kafka.order.avro.model.OrderApprovalStatus;
import dev.hectorolea.food.ordering.system.kafka.order.avro.model.RestaurantApprovalRequestAvroModel;
import dev.hectorolea.food.ordering.system.kafka.order.avro.model.RestaurantApprovalResponseAvroModel;
import dev.hectorolea.food.ordering.system.restaurant.service.domain.dto.RestaurantApprovalRequest;
import dev.hectorolea.food.ordering.system.restaurant.service.domain.entity.Product;
import dev.hectorolea.food.ordering.system.restaurant.service.domain.outbox.model.OrderEventPayload;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class RestaurantMessagingDataMapper {

  public RestaurantApprovalRequest restaurantApprovalRequestAvroModelToRestaurantApproval(
      RestaurantApprovalRequestAvroModel restaurantApprovalRequestAvroModel) {
    return RestaurantApprovalRequest.builder()
        .id(restaurantApprovalRequestAvroModel.getId())
        .sagaId(restaurantApprovalRequestAvroModel.getSagaId())
        .restaurantId(restaurantApprovalRequestAvroModel.getRestaurantId())
        .orderId(restaurantApprovalRequestAvroModel.getOrderId())
        .restaurantOrderStatus(
            RestaurantOrderStatus.valueOf(
                restaurantApprovalRequestAvroModel.getRestaurantOrderStatus().name()))
        .products(
            restaurantApprovalRequestAvroModel.getProducts().stream()
                .map(
                    avroModel ->
                        Product.builder()
                            .productId(new ProductId(UUID.fromString(avroModel.getId())))
                            .quantity(avroModel.getQuantity())
                            .build())
                .collect(Collectors.toList()))
        .price(restaurantApprovalRequestAvroModel.getPrice())
        .createdAt(restaurantApprovalRequestAvroModel.getCreatedAt())
        .build();
  }

  public RestaurantApprovalResponseAvroModel orderEventPayloadToRestaurantApprovalResponseAvroModel(
      String sagaId, OrderEventPayload orderEventPayload) {
    return RestaurantApprovalResponseAvroModel.newBuilder()
        .setId(randomUUID().toString())
        .setSagaId(sagaId)
        .setOrderId(orderEventPayload.getOrderId())
        .setRestaurantId(orderEventPayload.getRestaurantId())
        .setCreatedAt(orderEventPayload.getCreatedAt().toInstant())
        .setOrderApprovalStatus(
            OrderApprovalStatus.valueOf(orderEventPayload.getOrderApprovalStatus()))
        .setFailureMessages(orderEventPayload.getFailureMessages())
        .build();
  }
}
