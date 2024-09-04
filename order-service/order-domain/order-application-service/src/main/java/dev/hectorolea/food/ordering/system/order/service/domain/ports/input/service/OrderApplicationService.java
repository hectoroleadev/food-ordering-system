package dev.hectorolea.food.ordering.system.order.service.domain.ports.input.service;

import dev.hectorolea.food.ordering.system.order.service.domain.dto.create.CreateOrderCommand;
import dev.hectorolea.food.ordering.system.order.service.domain.dto.create.CreateOrderResponse;
import dev.hectorolea.food.ordering.system.order.service.domain.dto.track.TrackOrderQuery;
import dev.hectorolea.food.ordering.system.order.service.domain.dto.track.TrackOrderResponse;
import javax.validation.Valid;

public interface OrderApplicationService {

  CreateOrderResponse createOrder(@Valid CreateOrderCommand createOrderCommand);

  TrackOrderResponse trackOrder(@Valid TrackOrderQuery trackOrderQuery);
}
