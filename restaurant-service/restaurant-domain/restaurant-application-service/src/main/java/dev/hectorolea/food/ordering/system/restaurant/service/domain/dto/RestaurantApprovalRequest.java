package dev.hectorolea.food.ordering.system.restaurant.service.domain.dto;

import dev.hectorolea.food.ordering.system.domain.valueobject.RestaurantOrderStatus;
import dev.hectorolea.food.ordering.system.restaurant.service.domain.entity.Product;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class RestaurantApprovalRequest {
  private String id;
  private String sagaId;
  private String restaurantId;
  private String orderId;
  private RestaurantOrderStatus restaurantOrderStatus;
  private List<Product> products;
  private java.math.BigDecimal price;
  private java.time.Instant createdAt;
}
