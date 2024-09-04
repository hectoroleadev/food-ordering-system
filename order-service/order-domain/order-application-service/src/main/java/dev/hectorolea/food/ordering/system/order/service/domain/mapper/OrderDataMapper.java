package dev.hectorolea.food.ordering.system.order.service.domain.mapper;

import static java.util.UUID.randomUUID;
import static java.util.stream.Collectors.toList;

import dev.hectorolea.food.ordering.system.domain.valueobject.CustomerId;
import dev.hectorolea.food.ordering.system.domain.valueobject.Money;
import dev.hectorolea.food.ordering.system.domain.valueobject.ProductId;
import dev.hectorolea.food.ordering.system.domain.valueobject.RestaurantId;
import dev.hectorolea.food.ordering.system.order.service.domain.dto.create.CreateOrderCommand;
import dev.hectorolea.food.ordering.system.order.service.domain.dto.create.CreateOrderResponse;
import dev.hectorolea.food.ordering.system.order.service.domain.dto.create.OrderAddress;
import dev.hectorolea.food.ordering.system.order.service.domain.dto.track.TrackOrderResponse;
import dev.hectorolea.food.ordering.system.order.service.domain.entity.Order;
import dev.hectorolea.food.ordering.system.order.service.domain.entity.OrderItem;
import dev.hectorolea.food.ordering.system.order.service.domain.entity.Product;
import dev.hectorolea.food.ordering.system.order.service.domain.entity.Restaurant;
import dev.hectorolea.food.ordering.system.order.service.domain.valueobject.StreetAddress;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class OrderDataMapper {

  public Restaurant createOrderCommandToRestaurant(CreateOrderCommand createOrderCommand) {
    return Restaurant.builder()
        .restaurantId(new RestaurantId(createOrderCommand.getRestaurantId()))
        .products(
            createOrderCommand.getItems().stream()
                .map(orderItem -> new Product(new ProductId(orderItem.getProductId())))
                .collect(toList()))
        .build();
  }

  public Order createOrderCommandToOrder(CreateOrderCommand createOrderCommand) {
    return Order.builder()
        .customerId(new CustomerId(createOrderCommand.getCustomerId()))
        .restaurantId(new RestaurantId(createOrderCommand.getRestaurantId()))
        .deliveryAddress(orderAddressToStreetAddress(createOrderCommand.getAddress()))
        .price(new Money(createOrderCommand.getPrice()))
        .items(orderItemsToOrderItemEntities(createOrderCommand.getItems()))
        .build();
  }

  public CreateOrderResponse orderToCreateOrderResponse(Order order, String message) {
    return CreateOrderResponse.builder()
        .orderTackingId(order.getTrackingId().getValue())
        .orderStatus(order.getOrderStatus())
        .message(message)
        .build();
  }

  public TrackOrderResponse orderToTrackOrderResponse(Order order) {
    return TrackOrderResponse.builder()
        .orderTrackingId(order.getTrackingId().getValue())
        .orderStatus(order.getOrderStatus())
        .failureMessages(order.getFailureMessages())
        .build();
  }

  private List<OrderItem> orderItemsToOrderItemEntities(
      List<dev.hectorolea.food.ordering.system.order.service.domain.dto.create.OrderItem>
          orderItems) {
    return orderItems.stream()
        .map(
            orderItem ->
                OrderItem.builder()
                    .product(new Product(new ProductId(orderItem.getProductId())))
                    .price(new Money(orderItem.getPrice()))
                    .quantity(orderItem.getQuantity())
                    .subTotal(new Money(orderItem.getSubTotal()))
                    .build())
        .collect(toList());
  }

  private StreetAddress orderAddressToStreetAddress(OrderAddress orderAddress) {
    return new StreetAddress(
        randomUUID(),
        orderAddress.getStreet(),
        orderAddress.getPostalCode(),
        orderAddress.getCity());
  }
}
