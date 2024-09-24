package dev.hectorolea.food.ordering.system.restaurant.service.domain;

import dev.hectorolea.food.ordering.system.domain.event.publisher.DomainEventPublisher;
import dev.hectorolea.food.ordering.system.restaurant.service.domain.entity.Restaurant;
import dev.hectorolea.food.ordering.system.restaurant.service.domain.event.OrderApprovalEvent;
import dev.hectorolea.food.ordering.system.restaurant.service.domain.event.OrderApprovedEvent;
import dev.hectorolea.food.ordering.system.restaurant.service.domain.event.OrderRejectedEvent;
import java.util.List;

public interface RestaurantDomainService {

  OrderApprovalEvent validateOrder(Restaurant restaurant, List<String> failureMessages);
}
