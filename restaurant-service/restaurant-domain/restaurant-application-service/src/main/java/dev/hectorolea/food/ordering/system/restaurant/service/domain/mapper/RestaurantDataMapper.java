package dev.hectorolea.food.ordering.system.restaurant.service.domain.mapper;

import static java.util.stream.Collectors.toList;

import dev.hectorolea.food.ordering.system.domain.valueobject.Money;
import dev.hectorolea.food.ordering.system.domain.valueobject.OrderId;
import dev.hectorolea.food.ordering.system.domain.valueobject.OrderStatus;
import dev.hectorolea.food.ordering.system.domain.valueobject.RestaurantId;
import dev.hectorolea.food.ordering.system.restaurant.service.domain.dto.RestaurantApprovalRequest;
import dev.hectorolea.food.ordering.system.restaurant.service.domain.entity.OrderDetail;
import dev.hectorolea.food.ordering.system.restaurant.service.domain.entity.Product;
import dev.hectorolea.food.ordering.system.restaurant.service.domain.entity.Restaurant;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class RestaurantDataMapper {
  public Restaurant restaurantApprovalRequestToRestaurant(
      RestaurantApprovalRequest restaurantApprovalRequest) {
    return Restaurant.builder()
        .restaurantId(
            new RestaurantId(UUID.fromString(restaurantApprovalRequest.getRestaurantId())))
        .orderDetail(
            OrderDetail.builder()
                .orderId(new OrderId(UUID.fromString(restaurantApprovalRequest.getOrderId())))
                .products(
                    restaurantApprovalRequest.getProducts().stream()
                        .map(
                            product ->
                                Product.builder()
                                    .productId(product.getId())
                                    .quantity(product.getQuantity())
                                    .build())
                        .collect(toList()))
                .totalAmount(new Money(restaurantApprovalRequest.getPrice()))
                .orderStatus(
                    OrderStatus.valueOf(
                        restaurantApprovalRequest.getRestaurantOrderStatus().name()))
                .build())
        .build();
  }
}
