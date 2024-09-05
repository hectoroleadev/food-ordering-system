package dev.hectorolea.food.ordering.system.order.service.dataaccess.order.mapper;

import static dev.hectorolea.food.ordering.system.order.service.domain.entity.Order.FAILURE_MESSAGE_DELIMITER;
import static java.lang.String.join;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;

import dev.hectorolea.food.ordering.system.domain.valueobject.CustomerId;
import dev.hectorolea.food.ordering.system.domain.valueobject.Money;
import dev.hectorolea.food.ordering.system.domain.valueobject.OrderId;
import dev.hectorolea.food.ordering.system.domain.valueobject.ProductId;
import dev.hectorolea.food.ordering.system.domain.valueobject.RestaurantId;
import dev.hectorolea.food.ordering.system.order.service.dataaccess.order.entity.OrderAddressEntity;
import dev.hectorolea.food.ordering.system.order.service.dataaccess.order.entity.OrderEntity;
import dev.hectorolea.food.ordering.system.order.service.dataaccess.order.entity.OrderItemEntity;
import dev.hectorolea.food.ordering.system.order.service.domain.entity.Order;
import dev.hectorolea.food.ordering.system.order.service.domain.entity.OrderItem;
import dev.hectorolea.food.ordering.system.order.service.domain.entity.Product;
import dev.hectorolea.food.ordering.system.order.service.domain.valueobject.OrderItemId;
import dev.hectorolea.food.ordering.system.order.service.domain.valueobject.StreetAddress;
import dev.hectorolea.food.ordering.system.order.service.domain.valueobject.TrackingId;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class OrderDataAccessMapper {

  public OrderEntity orderToOrderEntity(Order order) {
    OrderEntity orderEntity =
        OrderEntity.builder()
            .id(order.getId().getValue())
            .customerId(order.getCustomerId().getValue())
            .restaurantId(order.getRestaurantId().getValue())
            .trackingId(order.getTrackingId().getValue())
            .address(deliveryAddressToAddressEntity(order.getDeliveryAddress()))
            .price(order.getPrice().getAmount())
            .items(orderItemsToOrderItemEntities(order.getItems()))
            .orderStatus(order.getOrderStatus())
            .failureMessages(
                order.getFailureMessages() != null
                    ? join(FAILURE_MESSAGE_DELIMITER, order.getFailureMessages())
                    : "")
            .build();
    orderEntity.getAddress().setOrder(orderEntity);
    orderEntity.getItems().forEach(orderItemEntity -> orderItemEntity.setOrder(orderEntity));

    return orderEntity;
  }

  public Order orderEntityToOrder(OrderEntity orderEntity) {
    return Order.builder()
        .orderId(new OrderId(orderEntity.getId()))
        .customerId(new CustomerId(orderEntity.getCustomerId()))
        .restaurantId(new RestaurantId(orderEntity.getRestaurantId()))
        .deliveryAddress(addressEntityToDeliveryAddress(orderEntity.getAddress()))
        .price(new Money(orderEntity.getPrice()))
        .items(orderItemEntitiesToOrderItems(orderEntity.getItems()))
        .trackingId(new TrackingId(orderEntity.getTrackingId()))
        .orderStatus(orderEntity.getOrderStatus())
        .failureMessages(
            orderEntity.getFailureMessages().isEmpty()
                ? new ArrayList<>()
                : new ArrayList<>(
                    asList(
                        orderEntity.getFailureMessages().split(FAILURE_MESSAGE_DELIMITER))))
        .build();
  }

  private List<OrderItem> orderItemEntitiesToOrderItems(List<OrderItemEntity> items) {
    return items.stream()
        .map(
            orderItemEntity ->
                OrderItem.builder()
                    .orderItemId(new OrderItemId(orderItemEntity.getId()))
                    .product(new Product(new ProductId(orderItemEntity.getProductId())))
                    .price(new Money(orderItemEntity.getPrice()))
                    .quantity(orderItemEntity.getQuantity())
                    .subTotal(new Money(orderItemEntity.getSubTotal()))
                    .build())
        .collect(toList());
  }

  private StreetAddress addressEntityToDeliveryAddress(OrderAddressEntity address) {
    return new StreetAddress(
        address.getId(), address.getStreet(), address.getPostalCode(), address.getCity());
  }

  private List<OrderItemEntity> orderItemsToOrderItemEntities(List<OrderItem> items) {
    return items.stream()
        .map(
            orderItem ->
                OrderItemEntity.builder()
                    .id(orderItem.getId().getValue())
                    .productId(orderItem.getProduct().getId().getValue())
                    .price(orderItem.getPrice().getAmount())
                    .quantity(orderItem.getQuantity())
                    .subTotal(orderItem.getSubTotal().getAmount())
                    .build())
        .collect(toList());
  }

  private OrderAddressEntity deliveryAddressToAddressEntity(StreetAddress deliveryAddress) {
    return OrderAddressEntity.builder()
        .id(deliveryAddress.getId())
        .street(deliveryAddress.getStreet())
        .postalCode(deliveryAddress.getPostalCode())
        .city(deliveryAddress.getCity())
        .build();
  }
}
