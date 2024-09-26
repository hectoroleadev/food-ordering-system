package dev.hectorolea.food.ordering.system.order.service.dataaccess.customer.adapter;

import dev.hectorolea.food.ordering.system.order.service.domain.entity.Customer;
import dev.hectorolea.food.ordering.system.order.service.domain.ports.output.repository.CustomerRepository;
import dev.hectorolea.food.ordering.system.order.service.dataaccess.customer.mapper.CustomerDataAccessMapper;
import dev.hectorolea.food.ordering.system.order.service.dataaccess.customer.repository.CustomerJpaRepository;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class CustomerRepositoryImpl implements CustomerRepository {

  private final CustomerJpaRepository customerJpaRepository;
  private final CustomerDataAccessMapper customerDataAccessMapper;

  public CustomerRepositoryImpl(
      CustomerJpaRepository customerJpaRepository,
      CustomerDataAccessMapper customerDataAccessMapper) {
    this.customerJpaRepository = customerJpaRepository;
    this.customerDataAccessMapper = customerDataAccessMapper;
  }

  @Override
  public Optional<Customer> findCustomer(UUID customerId) {
    return customerJpaRepository
        .findById(customerId)
        .map(customerDataAccessMapper::customerEntityToCustomer);
  }
}
