package dev.hectorolea.food.ordering.system.restaurant.service.domain.exception;

import dev.hectorolea.food.ordering.system.domain.exception.DomainException;

public class RestaurantDomainException extends DomainException {
  public RestaurantDomainException(String message) {
    super(message);
  }

  public RestaurantDomainException(String message, Throwable cause) {
    super(message, cause);
  }
}
