package dev.hectorolea.food.ordering.system.order.service.application.rest;

import static org.springframework.http.ResponseEntity.ok;

import dev.hectorolea.food.ordering.system.order.service.domain.dto.create.CreateOrderCommand;
import dev.hectorolea.food.ordering.system.order.service.domain.dto.create.CreateOrderResponse;
import dev.hectorolea.food.ordering.system.order.service.domain.dto.track.TrackOrderQuery;
import dev.hectorolea.food.ordering.system.order.service.domain.dto.track.TrackOrderResponse;
import dev.hectorolea.food.ordering.system.order.service.domain.ports.input.service.OrderApplicationService;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping(value = "/orders", produces = "application/vnd.api.v1+json")
public class OrderController {

  private final OrderApplicationService orderApplicationService;

  public OrderController(OrderApplicationService orderApplicationService) {
    this.orderApplicationService = orderApplicationService;
  }

  @PostMapping()
  public ResponseEntity<CreateOrderResponse> createOrder(
      @RequestBody CreateOrderCommand createOrderCommand) {
    log.info(
        "Creating order for customer: {} at restaurant: {}",
        createOrderCommand.getCustomerId(),
        createOrderCommand.getRestaurantId());
    CreateOrderResponse createOrderResponse =
        orderApplicationService.createOrder(createOrderCommand);
    log.info("Order created with tracking id: {}", createOrderResponse.getOrderTrackingId());
    return ok(createOrderResponse);
  }

  @GetMapping("/{trackingId}")
  public ResponseEntity<TrackOrderResponse> getOrderByTrackingId(@PathVariable UUID trackingId) {
    TrackOrderResponse trackOrderResponse =
        orderApplicationService.trackOrder(
            TrackOrderQuery.builder().orderTrackingId(trackingId).build());
    log.info(
        "Returning order status with tracking id: {}", trackOrderResponse.getOrderTrackingId());
    return ok(trackOrderResponse);
  }
}
