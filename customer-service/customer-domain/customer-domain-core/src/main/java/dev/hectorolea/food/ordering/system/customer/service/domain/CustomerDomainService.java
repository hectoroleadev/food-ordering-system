package dev.hectorolea.food.ordering.system.customer.service.domain;

import dev.hectorolea.food.ordering.system.customer.service.domain.entity.Customer;
import dev.hectorolea.food.ordering.system.customer.service.domain.event.CustomerCreatedEvent;

public interface CustomerDomainService {

  CustomerCreatedEvent validateAndInitiateCustomer(Customer customer);
}
