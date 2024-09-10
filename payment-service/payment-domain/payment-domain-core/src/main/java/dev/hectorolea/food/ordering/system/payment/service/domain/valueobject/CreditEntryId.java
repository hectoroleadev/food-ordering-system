package dev.hectorolea.food.ordering.system.payment.service.domain.valueobject;

import dev.hectorolea.food.ordering.system.domain.valueobject.BaseId;
import java.util.UUID;

public class CreditEntryId extends BaseId<UUID> {
  public CreditEntryId(UUID value) {
    super(value);
  }
}
