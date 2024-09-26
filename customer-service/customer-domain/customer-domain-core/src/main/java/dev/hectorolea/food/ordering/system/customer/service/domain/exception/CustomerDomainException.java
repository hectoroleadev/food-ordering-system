package dev.hectorolea.food.ordering.system.customer.service.domain.exception;

import dev.hectorolea.food.ordering.system.domain.exception.DomainException;

public class CustomerDomainException extends DomainException {

  public CustomerDomainException(String message) {
    super(message);
  }
}
