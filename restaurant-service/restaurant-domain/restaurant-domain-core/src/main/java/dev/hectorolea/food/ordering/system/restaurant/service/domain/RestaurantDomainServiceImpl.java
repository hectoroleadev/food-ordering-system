package dev.hectorolea.food.ordering.system.restaurant.service.domain;

import static dev.hectorolea.food.ordering.system.domain.DomainConstants.UTC;
import static java.time.ZoneId.of;
import static java.time.ZonedDateTime.now;

import dev.hectorolea.food.ordering.system.domain.event.publisher.DomainEventPublisher;
import dev.hectorolea.food.ordering.system.domain.valueobject.OrderApprovalStatus;
import dev.hectorolea.food.ordering.system.restaurant.service.domain.entity.Restaurant;
import dev.hectorolea.food.ordering.system.restaurant.service.domain.event.OrderApprovalEvent;
import dev.hectorolea.food.ordering.system.restaurant.service.domain.event.OrderApprovedEvent;
import dev.hectorolea.food.ordering.system.restaurant.service.domain.event.OrderRejectedEvent;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RestaurantDomainServiceImpl implements RestaurantDomainService {

  @Override
  public OrderApprovalEvent validateOrder(
      Restaurant restaurant,
      List<String> failureMessages,
      DomainEventPublisher<OrderApprovedEvent> orderApprovedEventDomainEventPublisher,
      DomainEventPublisher<OrderRejectedEvent> orderRejectedEventDomainEventPublisher) {
    restaurant.validateOrder(failureMessages);
    log.info("Validating order with id: {}", restaurant.getOrderDetail().getId().getValue());

    if (failureMessages.isEmpty()) {
      log.info(
          "Order is approved for order id: {}", restaurant.getOrderDetail().getId().getValue());
      restaurant.constructOrderApproval(OrderApprovalStatus.APPROVED);
      return new OrderApprovedEvent(
          restaurant.getOrderApproval(),
          restaurant.getId(),
          failureMessages,
          now(of(UTC)),
          orderApprovedEventDomainEventPublisher);
    } else {
      log.info(
          "Order is rejected for order id: {}", restaurant.getOrderDetail().getId().getValue());
      restaurant.constructOrderApproval(OrderApprovalStatus.REJECTED);
      return new OrderRejectedEvent(
          restaurant.getOrderApproval(),
          restaurant.getId(),
          failureMessages,
          now(of(UTC)),
          orderRejectedEventDomainEventPublisher);
    }
  }
}
