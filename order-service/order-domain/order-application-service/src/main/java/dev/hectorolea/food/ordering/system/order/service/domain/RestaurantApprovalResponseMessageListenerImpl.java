package dev.hectorolea.food.ordering.system.order.service.domain;

import static dev.hectorolea.food.ordering.system.order.service.domain.entity.Order.FAILURE_MESSAGE_DELIMITER;
import static java.lang.String.join;

import dev.hectorolea.food.ordering.system.order.service.domain.dto.message.RestaurantApprovalResponse;
import dev.hectorolea.food.ordering.system.order.service.domain.event.OrderCancelledEvent;
import dev.hectorolea.food.ordering.system.order.service.domain.ports.input.message.listener.restaurantapproval.RestaurantApprovalResponseMessageListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Slf4j
@Validated
@Service
public class RestaurantApprovalResponseMessageListenerImpl
    implements RestaurantApprovalResponseMessageListener {

  private final OrderApprovalSaga orderApprovalSaga;

  public RestaurantApprovalResponseMessageListenerImpl(OrderApprovalSaga orderApprovalSaga) {
    this.orderApprovalSaga = orderApprovalSaga;
  }

  @Override
  public void orderApproved(RestaurantApprovalResponse restaurantApprovalResponse) {
    orderApprovalSaga.process(restaurantApprovalResponse);
    log.info("Order is approved for order id: {}", restaurantApprovalResponse.getOrderId());
  }

  @Override
  public void orderRejected(RestaurantApprovalResponse restaurantApprovalResponse) {
    OrderCancelledEvent domainEvent = orderApprovalSaga.rollback(restaurantApprovalResponse);
    log.info(
        "Publishing order cancelled event for order id: {} with failure messages: {}",
        restaurantApprovalResponse.getOrderId(),
        join(FAILURE_MESSAGE_DELIMITER, restaurantApprovalResponse.getFailureMessages()));
    domainEvent.fire();
  }
}