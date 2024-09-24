package dev.hectorolea.food.ordering.system.order.service.messaging.listener.kafka;

import dev.hectorolea.food.ordering.system.kafka.consumer.KafkaConsumer;
import dev.hectorolea.food.ordering.system.kafka.order.avro.model.PaymentResponseAvroModel;
import dev.hectorolea.food.ordering.system.kafka.order.avro.model.PaymentStatus;
import dev.hectorolea.food.ordering.system.order.service.domain.exception.OrderNotFoundException;
import dev.hectorolea.food.ordering.system.order.service.domain.ports.input.message.listener.payment.PaymentResponseMessageListener;
import dev.hectorolea.food.ordering.system.order.service.messaging.mapper.OrderMessagingDataMapper;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PaymentResponseKafkaListener implements KafkaConsumer<PaymentResponseAvroModel> {

  private final PaymentResponseMessageListener paymentResponseMessageListener;
  private final OrderMessagingDataMapper orderMessagingDataMapper;

  public PaymentResponseKafkaListener(
      PaymentResponseMessageListener paymentResponseMessageListener,
      OrderMessagingDataMapper orderMessagingDataMapper) {
    this.paymentResponseMessageListener = paymentResponseMessageListener;
    this.orderMessagingDataMapper = orderMessagingDataMapper;
  }

  @Override
  @KafkaListener(
      id = "${kafka-consumer-config.payment-consumer-group-id}",
      topics = "${order-service.payment-response-topic-name}")
  public void receive(
      @Payload List<PaymentResponseAvroModel> messages,
      @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) List<String> keys,
      @Header(KafkaHeaders.RECEIVED_PARTITION_ID) List<Integer> partitions,
      @Header(KafkaHeaders.OFFSET) List<Long> offsets) {
    log.info(
        "{} number of payment responses received with keys:{}, partitions:{} and offsets: {}",
        messages.size(),
        keys.toString(),
        partitions.toString(),
        offsets.toString());

    messages.forEach(
        paymentResponseAvroModel -> {
          try {
            if (PaymentStatus.COMPLETED == paymentResponseAvroModel.getPaymentStatus()) {
              log.info(
                  "Processing successful payment for order id: {}",
                  paymentResponseAvroModel.getOrderId());
              paymentResponseMessageListener.paymentCompleted(
                  orderMessagingDataMapper.paymentResponseAvroModelToPaymentResponse(
                      paymentResponseAvroModel));
            } else if (PaymentStatus.CANCELLED == paymentResponseAvroModel.getPaymentStatus()
                || PaymentStatus.FAILED == paymentResponseAvroModel.getPaymentStatus()) {
              log.info(
                  "Processing unsuccessful payment for order id: {}",
                  paymentResponseAvroModel.getOrderId());
              paymentResponseMessageListener.paymentCancelled(
                  orderMessagingDataMapper.paymentResponseAvroModelToPaymentResponse(
                      paymentResponseAvroModel));
            }
          } catch (OptimisticLockingFailureException e) {
            // NO-OP for optimistic lock. This means another thread finished the work, do not throw
            // error to prevent reading the data from kafka again!
            log.error(
                "Caught optimistic locking exception in PaymentResponseKafkaListener for order id: {}",
                paymentResponseAvroModel.getOrderId());
          } catch (OrderNotFoundException e) {
            // NO-OP for OrderNotFoundException
            log.error("No order found for order id: {}", paymentResponseAvroModel.getOrderId());
          }
        });
  }
}
