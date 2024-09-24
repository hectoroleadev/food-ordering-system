package dev.hectorolea.food.ordering.system.restaurant.service.domain.event;

import dev.hectorolea.food.ordering.system.domain.valueobject.RestaurantId;
import dev.hectorolea.food.ordering.system.restaurant.service.domain.entity.OrderApproval;
import java.time.ZonedDateTime;
import java.util.List;

public class OrderRejectedEvent extends OrderApprovalEvent {

  public OrderRejectedEvent(
      OrderApproval orderApproval,
      RestaurantId restaurantId,
      List<String> failureMessages,
      ZonedDateTime createdAt) {
    super(orderApproval, restaurantId, failureMessages, createdAt);
  }
}
