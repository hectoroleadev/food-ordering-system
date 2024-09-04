package dev.hectorolea.food.ordering.system.order.service.domain.exception;

import dev.hectorolea.food.ordering.system.domain.exception.DomainException;

public class OrderNotFoundException extends DomainException {

  public OrderNotFoundException(String message) {
    super(message);
  }

  public OrderNotFoundException(String message, Throwable cause) {
    super(message, cause);
  }
}
