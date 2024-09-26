package dev.hectorolea.food.ordering.system.customer.service.application.handler;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

import dev.hectorolea.food.ordering.system.application.handler.ErrorDTO;
import dev.hectorolea.food.ordering.system.application.handler.GlobalExceptionHandler;
import dev.hectorolea.food.ordering.system.customer.service.domain.exception.CustomerDomainException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@Slf4j
@ControllerAdvice
public class CustomerGlobalExceptionHandler extends GlobalExceptionHandler {

  @ResponseBody
  @ExceptionHandler(value = {CustomerDomainException.class})
  @ResponseStatus(BAD_REQUEST)
  public ErrorDTO handleException(CustomerDomainException exception) {
    log.error(exception.getMessage(), exception);
    return ErrorDTO.builder()
        .code(BAD_REQUEST.getReasonPhrase())
        .message(exception.getMessage())
        .build();
  }
}
