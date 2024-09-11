package dev.hectorolea.food.ordering.system.restaurant.service.domain.valueobject;

import dev.hectorolea.food.ordering.system.domain.valueobject.BaseId;
import java.util.UUID;

public class OrderApprovalId extends BaseId<UUID> {
  public OrderApprovalId(UUID value) {
    super(value);
  }
}
