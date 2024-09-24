package dev.hectorolea.food.ordering.system.payment.service.domain.messaging.publisher.kafka;

import dev.hectorolea.food.ordering.system.kafka.order.avro.model.PaymentResponseAvroModel;
import dev.hectorolea.food.ordering.system.kafka.producer.KafkaMessageHelper;
import dev.hectorolea.food.ordering.system.kafka.producer.service.KafkaProducer;
import dev.hectorolea.food.ordering.system.outbox.OutboxStatus;
import dev.hectorolea.food.ordering.system.payment.service.domain.config.PaymentServiceConfigData;
import dev.hectorolea.food.ordering.system.payment.service.domain.messaging.mapper.PaymentMessagingDataMapper;
import dev.hectorolea.food.ordering.system.payment.service.domain.outbox.model.OrderEventPayload;
import dev.hectorolea.food.ordering.system.payment.service.domain.outbox.model.OrderOutboxMessage;
import dev.hectorolea.food.ordering.system.payment.service.domain.ports.output.message.publisher.PaymentResponseMessagePublisher;
import java.util.function.BiConsumer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PaymentEventKafkaPublisher implements PaymentResponseMessagePublisher {

  private final PaymentMessagingDataMapper paymentMessagingDataMapper;
  private final KafkaProducer<String, PaymentResponseAvroModel> kafkaProducer;
  private final PaymentServiceConfigData paymentServiceConfigData;
  private final KafkaMessageHelper kafkaMessageHelper;

  public PaymentEventKafkaPublisher(
      PaymentMessagingDataMapper paymentMessagingDataMapper,
      KafkaProducer<String, PaymentResponseAvroModel> kafkaProducer,
      PaymentServiceConfigData paymentServiceConfigData,
      KafkaMessageHelper kafkaMessageHelper) {
    this.paymentMessagingDataMapper = paymentMessagingDataMapper;
    this.kafkaProducer = kafkaProducer;
    this.paymentServiceConfigData = paymentServiceConfigData;
    this.kafkaMessageHelper = kafkaMessageHelper;
  }

  @Override
  public void publish(
      OrderOutboxMessage orderOutboxMessage,
      BiConsumer<OrderOutboxMessage, OutboxStatus> outboxCallback) {
    OrderEventPayload orderEventPayload =
        kafkaMessageHelper.getOrderEventPayload(
            orderOutboxMessage.getPayload(), OrderEventPayload.class);

    String sagaId = orderOutboxMessage.getSagaId().toString();

    log.info(
        "Received OrderOutboxMessage for order id: {} and saga id: {}",
        orderEventPayload.getOrderId(),
        sagaId);

    try {
      PaymentResponseAvroModel paymentResponseAvroModel =
          paymentMessagingDataMapper.orderEventPayloadToPaymentResponseAvroModel(
              sagaId, orderEventPayload);

      kafkaProducer.send(
          paymentServiceConfigData.getPaymentResponseTopicName(),
          sagaId,
          paymentResponseAvroModel,
          kafkaMessageHelper.getKafkaCallback(
              paymentServiceConfigData.getPaymentResponseTopicName(),
              paymentResponseAvroModel,
              orderOutboxMessage,
              outboxCallback,
              orderEventPayload.getOrderId(),
              "PaymentResponseAvroModel"));

      log.info(
          "PaymentResponseAvroModel sent to kafka for order id: {} and saga id: {}",
          paymentResponseAvroModel.getOrderId(),
          sagaId);
    } catch (Exception e) {
      log.error(
          "Error while sending PaymentRequestAvroModel message"
              + " to kafka with order id: {} and saga id: {}, error: {}",
          orderEventPayload.getOrderId(),
          sagaId,
          e.getMessage());
    }
  }
}
