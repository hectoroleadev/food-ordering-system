package dev.hectorolea.food.ordering.system.customer.service.domain.create;

import java.util.UUID;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class CreateCustomerCommand {
  @NotNull private final UUID customerId;
  @NotNull private final String username;
  @NotNull private final String firstName;
  @NotNull private final String lastName;
}
