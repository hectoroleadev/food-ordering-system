package dev.hectorolea.food.ordering.system.order.service.domain.dto.create;

import dev.hectorolea.food.ordering.system.domain.valueobject.OrderStatus;
import java.util.UUID;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class CreateOrderResponse {
  @NotNull private final UUID orderTrackingId;
  @NotNull private final OrderStatus orderStatus;
  @NotNull private final String message;
}
