package dev.hectorolea.food.ordering.system.payment.service.domain.messaging.listener.kafka;

import static java.util.Objects.nonNull;

import dev.hectorolea.food.ordering.system.kafka.consumer.KafkaConsumer;
import dev.hectorolea.food.ordering.system.kafka.order.avro.model.PaymentOrderStatus;
import dev.hectorolea.food.ordering.system.kafka.order.avro.model.PaymentRequestAvroModel;
import dev.hectorolea.food.ordering.system.payment.service.domain.exception.PaymentApplicationServiceException;
import dev.hectorolea.food.ordering.system.payment.service.domain.exception.PaymentNotFoundException;
import dev.hectorolea.food.ordering.system.payment.service.domain.messaging.mapper.PaymentMessagingDataMapper;
import dev.hectorolea.food.ordering.system.payment.service.domain.ports.input.message.listener.PaymentRequestMessageListener;
import java.sql.SQLException;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.postgresql.util.PSQLState;
import org.springframework.dao.DataAccessException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PaymentRequestKafkaListener implements KafkaConsumer<PaymentRequestAvroModel> {

  private final PaymentRequestMessageListener paymentRequestMessageListener;
  private final PaymentMessagingDataMapper paymentMessagingDataMapper;

  public PaymentRequestKafkaListener(
      PaymentRequestMessageListener paymentRequestMessageListener,
      PaymentMessagingDataMapper paymentMessagingDataMapper) {
    this.paymentRequestMessageListener = paymentRequestMessageListener;
    this.paymentMessagingDataMapper = paymentMessagingDataMapper;
  }

  @Override
  @KafkaListener(
      id = "${kafka-consumer-config.payment-consumer-group-id}",
      topics = "${payment-service.payment-request-topic-name}")
  public void receive(
      @Payload List<PaymentRequestAvroModel> messages,
      @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) List<String> keys,
      @Header(KafkaHeaders.RECEIVED_PARTITION_ID) List<Integer> partitions,
      @Header(KafkaHeaders.OFFSET) List<Long> offsets) {
    log.info(
        "{} number of payment requests received with keys:{}, partitions:{} and offsets: {}",
        messages.size(),
        keys.toString(),
        partitions.toString(),
        offsets.toString());

    messages.forEach(
        paymentRequestAvroModel -> {
          try {
            if (PaymentOrderStatus.PENDING == paymentRequestAvroModel.getPaymentOrderStatus()) {
              log.info("Processing payment for order id: {}", paymentRequestAvroModel.getOrderId());
              paymentRequestMessageListener.completePayment(
                  paymentMessagingDataMapper.paymentRequestAvroModelToPaymentRequest(
                      paymentRequestAvroModel));
            } else if (PaymentOrderStatus.CANCELLED
                == paymentRequestAvroModel.getPaymentOrderStatus()) {
              log.info("Cancelling payment for order id: {}", paymentRequestAvroModel.getOrderId());
              paymentRequestMessageListener.cancelPayment(
                  paymentMessagingDataMapper.paymentRequestAvroModelToPaymentRequest(
                      paymentRequestAvroModel));
            }
          } catch (DataAccessException e) {
            SQLException sqlException = (SQLException) e.getRootCause();
            if (nonNull(sqlException)
                && nonNull(sqlException.getSQLState())
                && PSQLState.UNIQUE_VIOLATION.getState().equals(sqlException.getSQLState())) {
              // NO-OP for unique constraint exception
              log.error(
                  "Caught unique constraint exception with sql state: {} "
                      + "in PaymentRequestKafkaListener for order id: {}",
                  sqlException.getSQLState(),
                  paymentRequestAvroModel.getOrderId());
            } else {
              throw new PaymentApplicationServiceException(
                  "Throwing DataAccessException in"
                      + " PaymentRequestKafkaListener: "
                      + e.getMessage(),
                  e);
            }
          } catch (PaymentNotFoundException e) {
            // NO-OP for PaymentNotFoundException
            log.error("No payment found for order id: {}", paymentRequestAvroModel.getOrderId());
          }
        });
  }
}
