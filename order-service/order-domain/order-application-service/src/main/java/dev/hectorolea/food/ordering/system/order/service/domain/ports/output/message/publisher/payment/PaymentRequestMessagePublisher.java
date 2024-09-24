package dev.hectorolea.food.ordering.system.order.service.domain.ports.output.message.publisher.payment;

import dev.hectorolea.food.ordering.system.order.service.domain.outbox.model.payment.OrderPaymentOutboxMessage;
import dev.hectorolea.food.ordering.system.outbox.OutboxStatus;
import java.util.function.BiConsumer;

public interface PaymentRequestMessagePublisher {

  void publish(
      OrderPaymentOutboxMessage orderPaymentOutboxMessage,
      BiConsumer<OrderPaymentOutboxMessage, OutboxStatus> outboxCallback);
}
