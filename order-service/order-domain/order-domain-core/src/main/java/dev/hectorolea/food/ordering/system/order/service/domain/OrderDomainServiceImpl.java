package dev.hectorolea.food.ordering.system.order.service.domain;

import static dev.hectorolea.food.ordering.system.domain.DomainConstants.UTC;
import static java.lang.String.format;
import static java.time.ZoneId.of;
import static java.time.ZonedDateTime.now;

import dev.hectorolea.food.ordering.system.order.service.domain.entity.Order;
import dev.hectorolea.food.ordering.system.order.service.domain.entity.Product;
import dev.hectorolea.food.ordering.system.order.service.domain.entity.Restaurant;
import dev.hectorolea.food.ordering.system.order.service.domain.event.OrderCancelledEvent;
import dev.hectorolea.food.ordering.system.order.service.domain.event.OrderCreatedEvent;
import dev.hectorolea.food.ordering.system.order.service.domain.event.OrderPaidEvent;
import dev.hectorolea.food.ordering.system.order.service.domain.exception.OrderDomainException;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class OrderDomainServiceImpl implements OrderDomainService {

  @Override
  public OrderCreatedEvent validateAndInitiateOrder(Order order, Restaurant restaurant) {
    validateRestaurant(restaurant);
    setOrderProductInformation(order, restaurant);
    order.validateOrder();
    order.initializeOrder();
    log.info("Order with id: {} is initiated", order.getId().getValue());
    return new OrderCreatedEvent(order, now(of(UTC)));
  }

  @Override
  public OrderPaidEvent payOrder(Order order) {
    order.pay();
    log.info("Order with id: {} is paid", order.getId().getValue());
    return new OrderPaidEvent(order, now(of(UTC)));
  }

  @Override
  public void approveOrder(Order order) {
    order.approve();
    log.info("Order with id: {} is approved", order.getId().getValue());
  }

  @Override
  public OrderCancelledEvent cancelOrderPayment(Order order, List<String> failureMessages) {
    order.initCancel(failureMessages);
    log.info("Order payment is cancelling for order id: {}", order.getId().getValue());
    return new OrderCancelledEvent(order, now(of(UTC)));
  }

  @Override
  public void cancelOrder(Order order, List<String> failureMessages) {
    order.cancel(failureMessages);
    log.info("Order with id: {} is cancelled", order.getId().getValue());
  }

  private void validateRestaurant(Restaurant restaurant) {
    if (!restaurant.isActive()) {
      throw new OrderDomainException(
          format("Restaurant with id %s is currently not active!", restaurant.getId().getValue()));
    }
  }

  private void setOrderProductInformation(Order order, Restaurant restaurant) {
    order
        .getItems()
        .forEach(
            orderItem ->
                restaurant
                    .getProducts()
                    .forEach(
                        restaurantProduct -> {
                          Product currentProduct = orderItem.getProduct();
                          if (currentProduct.equals(restaurantProduct)) {
                            currentProduct.updateWithConfirmedNameAndPrice(
                                restaurantProduct.getName(), restaurantProduct.getPrice());
                          }
                        }));
  }
}
