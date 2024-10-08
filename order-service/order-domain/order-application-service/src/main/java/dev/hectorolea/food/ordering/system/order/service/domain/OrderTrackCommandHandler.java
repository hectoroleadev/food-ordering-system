package dev.hectorolea.food.ordering.system.order.service.domain;

import dev.hectorolea.food.ordering.system.order.service.domain.dto.track.TrackOrderQuery;
import dev.hectorolea.food.ordering.system.order.service.domain.dto.track.TrackOrderResponse;
import dev.hectorolea.food.ordering.system.order.service.domain.entity.Order;
import dev.hectorolea.food.ordering.system.order.service.domain.exception.OrderNotFoundException;
import dev.hectorolea.food.ordering.system.order.service.domain.mapper.OrderDataMapper;
import dev.hectorolea.food.ordering.system.order.service.domain.ports.output.repository.OrderRepository;
import dev.hectorolea.food.ordering.system.order.service.domain.valueobject.TrackingId;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import static java.lang.String.format;

@Slf4j
@Component
public class OrderTrackCommandHandler {

  private final OrderDataMapper orderDataMapper;

  private final OrderRepository orderRepository;

  public OrderTrackCommandHandler(
      OrderDataMapper orderDataMapper, OrderRepository orderRepository) {
    this.orderDataMapper = orderDataMapper;
    this.orderRepository = orderRepository;
  }

  @Transactional(readOnly = true)
  public TrackOrderResponse trackOrder(TrackOrderQuery trackOrderQuery) {
    Optional<Order> orderResult =
        orderRepository.findByTrackingId(new TrackingId(trackOrderQuery.getOrderTrackingId()));
    if (orderResult.isEmpty()) {
      log.warn("Could not find order with tracking id: {}", trackOrderQuery.getOrderTrackingId());
      throw new OrderNotFoundException(
          format(
              "Could not find order with tracking id: %s", trackOrderQuery.getOrderTrackingId()));
    }
    return orderDataMapper.orderToTrackOrderResponse(orderResult.get());
  }
}
