package dev.hectorolea.food.ordering.system.order.service.domain.exception;

import dev.hectorolea.food.ordering.system.domain.exception.DomainException;

public class OrderDomainException extends DomainException {

  public OrderDomainException(String message) {
    super(message);
  }

  public OrderDomainException(String message, Throwable cause) {
    super(message, cause);
  }
}
