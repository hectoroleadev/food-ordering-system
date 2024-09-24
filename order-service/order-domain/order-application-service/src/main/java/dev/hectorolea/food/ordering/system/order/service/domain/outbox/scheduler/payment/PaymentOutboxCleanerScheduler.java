package dev.hectorolea.food.ordering.system.order.service.domain.outbox.scheduler.payment;

import static java.util.stream.Collectors.joining;

import dev.hectorolea.food.ordering.system.order.service.domain.outbox.model.payment.OrderPaymentOutboxMessage;
import dev.hectorolea.food.ordering.system.outbox.OutboxScheduler;
import dev.hectorolea.food.ordering.system.outbox.OutboxStatus;
import dev.hectorolea.food.ordering.system.saga.SagaStatus;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PaymentOutboxCleanerScheduler implements OutboxScheduler {

  private final PaymentOutboxHelper paymentOutboxHelper;

  public PaymentOutboxCleanerScheduler(PaymentOutboxHelper paymentOutboxHelper) {
    this.paymentOutboxHelper = paymentOutboxHelper;
  }

  @Override
  @Scheduled(cron = "@midnight")
  public void processOutboxMessage() {
    Optional<List<OrderPaymentOutboxMessage>> outboxMessagesResponse =
        paymentOutboxHelper.getPaymentOutboxMessageByOutboxStatusAndSagaStatus(
            OutboxStatus.COMPLETED,
            SagaStatus.SUCCEEDED,
            SagaStatus.FAILED,
            SagaStatus.COMPENSATED);

    if (outboxMessagesResponse.isPresent()) {
      List<OrderPaymentOutboxMessage> outboxMessages = outboxMessagesResponse.get();
      log.info(
          "Received {} OrderPaymentOutboxMessage for clean-up. The payloads: {}",
          outboxMessages.size(),
          outboxMessages.stream()
              .map(OrderPaymentOutboxMessage::getPayload)
              .collect(joining("\n")));
      paymentOutboxHelper.deletePaymentOutboxMessageByOutboxStatusAndSagaStatus(
          OutboxStatus.COMPLETED, SagaStatus.SUCCEEDED, SagaStatus.FAILED, SagaStatus.COMPENSATED);
      log.info("{} OrderPaymentOutboxMessage deleted!", outboxMessages.size());
    }
  }
}
