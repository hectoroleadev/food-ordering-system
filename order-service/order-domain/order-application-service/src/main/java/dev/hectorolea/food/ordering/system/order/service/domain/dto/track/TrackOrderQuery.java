package dev.hectorolea.food.ordering.system.order.service.domain.dto.track;

import java.util.UUID;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class TrackOrderQuery {
  @NotNull private final UUID orderTrackingId;
}
