package dev.hectorolea.food.ordering.system.order.service.application.exception.handler;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

import dev.hectorolea.food.ordering.system.application.handler.ErrorDTO;
import dev.hectorolea.food.ordering.system.application.handler.GlobalExceptionHandler;
import dev.hectorolea.food.ordering.system.order.service.domain.exception.OrderDomainException;
import dev.hectorolea.food.ordering.system.order.service.domain.exception.OrderNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@Slf4j
@ControllerAdvice
public class OrderGlobalExceptionHandler extends GlobalExceptionHandler {

  @ResponseBody
  @ExceptionHandler(value = {OrderDomainException.class})
  @ResponseStatus(BAD_REQUEST)
  public ErrorDTO handleException(OrderDomainException orderDomainException) {
    log.error(orderDomainException.getMessage(), orderDomainException);
    return ErrorDTO.builder()
        .code(BAD_REQUEST.getReasonPhrase())
        .message(orderDomainException.getMessage())
        .build();
  }

  @ResponseBody
  @ExceptionHandler(value = {OrderNotFoundException.class})
  @ResponseStatus(NOT_FOUND)
  public ErrorDTO handleException(OrderNotFoundException orderNotFoundException) {
    log.error(orderNotFoundException.getMessage(), orderNotFoundException);
    return ErrorDTO.builder()
        .code(NOT_FOUND.getReasonPhrase())
        .message(orderNotFoundException.getMessage())
        .build();
  }
}
