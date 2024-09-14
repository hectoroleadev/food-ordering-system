package dev.hectorolea.food.ordering.system.order.service.domain.ports.output.repository;

import dev.hectorolea.food.ordering.system.domain.valueobject.OrderId;
import dev.hectorolea.food.ordering.system.order.service.domain.entity.Order;
import dev.hectorolea.food.ordering.system.order.service.domain.valueobject.TrackingId;
import java.util.Optional;

public interface OrderRepository {

  Order save(Order order);

  Optional<Order> findByTrackingId(TrackingId trackingId);

  Optional<Order> findById(OrderId orderId);
}
