package dev.hectorolea.food.ordering.system.restaurant.service.messaging.mapper;

import static java.util.UUID.randomUUID;
import static java.util.stream.Collectors.toList;

import dev.hectorolea.food.ordering.system.domain.valueobject.ProductId;
import dev.hectorolea.food.ordering.system.domain.valueobject.RestaurantOrderStatus;
import dev.hectorolea.food.ordering.system.kafka.order.avro.model.OrderApprovalStatus;
import dev.hectorolea.food.ordering.system.kafka.order.avro.model.RestaurantApprovalRequestAvroModel;
import dev.hectorolea.food.ordering.system.kafka.order.avro.model.RestaurantApprovalResponseAvroModel;
import dev.hectorolea.food.ordering.system.restaurant.service.domain.dto.RestaurantApprovalRequest;
import dev.hectorolea.food.ordering.system.restaurant.service.domain.entity.Product;
import dev.hectorolea.food.ordering.system.restaurant.service.domain.event.OrderApprovedEvent;
import dev.hectorolea.food.ordering.system.restaurant.service.domain.event.OrderRejectedEvent;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class RestaurantMessagingDataMapper {
  public RestaurantApprovalResponseAvroModel
      orderApprovedEventToRestaurantApprovalResponseAvroModel(
          OrderApprovedEvent orderApprovedEvent) {
    return RestaurantApprovalResponseAvroModel.newBuilder()
        .setId(randomUUID().toString())
        .setSagaId("")
        .setOrderId(orderApprovedEvent.getOrderApproval().getOrderId().getValue().toString())
        .setRestaurantId(orderApprovedEvent.getRestaurantId().getValue().toString())
        .setCreatedAt(orderApprovedEvent.getCreatedAt().toInstant())
        .setOrderApprovalStatus(
            OrderApprovalStatus.valueOf(
                orderApprovedEvent.getOrderApproval().getApprovalStatus().name()))
        .setFailureMessages(orderApprovedEvent.getFailureMessages())
        .build();
  }

  public RestaurantApprovalResponseAvroModel
      orderRejectedEventToRestaurantApprovalResponseAvroModel(
          OrderRejectedEvent orderRejectedEvent) {
    return RestaurantApprovalResponseAvroModel.newBuilder()
        .setId(randomUUID().toString())
        .setSagaId("")
        .setOrderId(orderRejectedEvent.getOrderApproval().getOrderId().getValue().toString())
        .setRestaurantId(orderRejectedEvent.getRestaurantId().getValue().toString())
        .setCreatedAt(orderRejectedEvent.getCreatedAt().toInstant())
        .setOrderApprovalStatus(
            OrderApprovalStatus.valueOf(
                orderRejectedEvent.getOrderApproval().getApprovalStatus().name()))
        .setFailureMessages(orderRejectedEvent.getFailureMessages())
        .build();
  }

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
                .collect(toList()))
        .price(restaurantApprovalRequestAvroModel.getPrice())
        .createdAt(restaurantApprovalRequestAvroModel.getCreatedAt())
        .build();
  }
}
