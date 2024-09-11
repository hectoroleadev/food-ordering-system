package dev.hectorolea.food.ordering.system.restaurant.service.domain.entity;

import static java.util.UUID.randomUUID;

import dev.hectorolea.food.ordering.system.domain.entity.AggregateRoot;
import dev.hectorolea.food.ordering.system.domain.valueobject.Money;
import dev.hectorolea.food.ordering.system.domain.valueobject.OrderApprovalStatus;
import dev.hectorolea.food.ordering.system.domain.valueobject.OrderStatus;
import dev.hectorolea.food.ordering.system.domain.valueobject.RestaurantId;
import dev.hectorolea.food.ordering.system.restaurant.service.domain.valueobject.OrderApprovalId;
import java.util.List;

public class Restaurant extends AggregateRoot<RestaurantId> {
  private OrderApproval orderApproval;
  private boolean isActive;
  private final OrderDetail orderDetail;

  public void validateOrder(List<String> failureMessages) {
    if (orderDetail.getOrderStatus() != OrderStatus.PAID) {
      failureMessages.add("Payment is not completed for order: " + orderDetail.getId());
    }
    Money totalAmount =
        orderDetail.getProducts().stream()
            .map(
                product -> {
                  if (!product.isAvailable()) {
                    failureMessages.add(
                        "Product with id: " + product.getId().getValue() + " is not available");
                  }
                  return product.getPrice().multiply(product.getQuantity());
                })
            .reduce(Money.ZERO, Money::add);

    if (!totalAmount.equals(orderDetail.getTotalAmount())) {
      failureMessages.add("Price total is not correct for order: " + orderDetail.getId());
    }
  }

  public void constructOrderApproval(OrderApprovalStatus orderApprovalStatus) {
    this.orderApproval =
        OrderApproval.builder()
            .orderApprovalId(new OrderApprovalId(randomUUID()))
            .restaurantId(this.getId())
            .orderId(this.getOrderDetail().getId())
            .approvalStatus(orderApprovalStatus)
            .build();
  }

  public void setActive(boolean active) {
    this.isActive = active;
  }

  private Restaurant(Builder builder) {
    setId(builder.restaurantId);
    orderApproval = builder.orderApproval;
    isActive = builder.isActive;
    orderDetail = builder.orderDetail;
  }

  public static Builder builder() {
    return new Builder();
  }

  public OrderApproval getOrderApproval() {
    return orderApproval;
  }

  public boolean isActive() {
    return isActive;
  }

  public OrderDetail getOrderDetail() {
    return orderDetail;
  }

  public static final class Builder {
    private RestaurantId restaurantId;
    private OrderApproval orderApproval;
    private boolean isActive;
    private OrderDetail orderDetail;

    private Builder() {}

    public Builder restaurantId(RestaurantId val) {
      restaurantId = val;
      return this;
    }

    public Builder orderApproval(OrderApproval val) {
      orderApproval = val;
      return this;
    }

    public Builder isActive(boolean val) {
      isActive = val;
      return this;
    }

    public Builder orderDetail(OrderDetail val) {
      orderDetail = val;
      return this;
    }

    public Restaurant build() {
      return new Restaurant(this);
    }
  }
}
