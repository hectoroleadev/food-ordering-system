package dev.hectorolea.food.ordering.system.order.service.domain.dto.create;

import java.util.UUID;
import javax.validation.constraints.NotNull;

import dev.hectorolea.food.ordering.system.domain.valueobject.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class CreateOrderResponse {
  @NotNull private final UUID orderTackingId;
  @NotNull private final OrderStatus orderStatus;
  @NotNull private final String message;
}
