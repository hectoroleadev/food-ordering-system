package dev.hectorolea.food.ordering.system.order.service.domain;

import static dev.hectorolea.food.ordering.system.order.service.domain.entity.Order.FAILURE_MESSAGE_DELIMITER;
import static java.lang.String.join;

import dev.hectorolea.food.ordering.system.order.service.domain.dto.message.PaymentResponse;
import dev.hectorolea.food.ordering.system.order.service.domain.ports.input.message.listener.payment.PaymentResponseMessageListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Slf4j
@Validated
@Service
public class PaymentResponseMessageListenerImpl implements PaymentResponseMessageListener {
  private final OrderPaymentSaga orderPaymentSaga;

  public PaymentResponseMessageListenerImpl(OrderPaymentSaga orderPaymentSaga) {
    this.orderPaymentSaga = orderPaymentSaga;
  }

  @Override
  public void paymentCompleted(PaymentResponse paymentResponse) {
    orderPaymentSaga.process(paymentResponse);
    log.info(
        "Order Payment Saga process operation is completed OrderPaidEvent for order id: {}",
        paymentResponse.getOrderId());
  }

  @Override
  public void paymentCancelled(PaymentResponse paymentResponse) {
    orderPaymentSaga.rollback(paymentResponse);
    log.info(
        "Order is roll backed for order id: {} with failure messages: {}",
        paymentResponse.getOrderId(),
        join(FAILURE_MESSAGE_DELIMITER, paymentResponse.getFailureMessages()));
  }
}
