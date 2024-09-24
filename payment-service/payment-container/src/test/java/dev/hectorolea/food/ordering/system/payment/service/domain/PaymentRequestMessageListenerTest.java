package dev.hectorolea.food.ordering.system.payment.service.domain;

import static dev.hectorolea.food.ordering.system.saga.order.SagaConstants.ORDER_SAGA_NAME;
import static java.time.Instant.now;
import static java.util.UUID.fromString;
import static java.util.UUID.randomUUID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import dev.hectorolea.food.ordering.system.domain.valueobject.PaymentStatus;
import dev.hectorolea.food.ordering.system.outbox.OutboxStatus;
import dev.hectorolea.food.ordering.system.payment.service.dataaccess.outbox.entity.OrderOutboxEntity;
import dev.hectorolea.food.ordering.system.payment.service.dataaccess.outbox.repository.OrderOutboxJpaRepository;
import dev.hectorolea.food.ordering.system.payment.service.domain.dto.PaymentRequest;
import dev.hectorolea.food.ordering.system.payment.service.domain.ports.input.message.listener.PaymentRequestMessageListener;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.postgresql.util.PSQLException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;

@Slf4j
@SpringBootTest(classes = PaymentServiceApplication.class)
public class PaymentRequestMessageListenerTest {

  @Autowired private PaymentRequestMessageListener paymentRequestMessageListener;

  @Autowired private OrderOutboxJpaRepository orderOutboxJpaRepository;

  private static final String CUSTOMER_ID = "d215b5f8-0249-4dc5-89a3-51fd148cfb41";
  private static final BigDecimal PRICE = new BigDecimal("100");

  @Test
  void testDoublePayment() {
    String sagaId = randomUUID().toString();
    paymentRequestMessageListener.completePayment(getPaymentRequest(sagaId));
    try {
      paymentRequestMessageListener.completePayment(getPaymentRequest(sagaId));
    } catch (DataAccessException e) {
      log.error(
          "DataAccessException occurred with sql state: {}",
          ((PSQLException) Objects.requireNonNull(e.getRootCause())).getSQLState());
    }
    assertOrderOutbox(sagaId);
  }

  @Test
  void testDoublePaymentWithThreads() {
    String sagaId = randomUUID().toString();
    ExecutorService executor = null;

    try {
      executor = Executors.newFixedThreadPool(2);
      List<Callable<Object>> tasks = new ArrayList<>();

      tasks.add(
          Executors.callable(
              () -> {
                try {
                  paymentRequestMessageListener.completePayment(getPaymentRequest(sagaId));
                } catch (DataAccessException e) {
                  log.error(
                      "DataAccessException occurred for thread 1 with sql state: {}",
                      ((PSQLException) Objects.requireNonNull(e.getRootCause())).getSQLState());
                }
              }));

      tasks.add(
          Executors.callable(
              () -> {
                try {
                  paymentRequestMessageListener.completePayment(getPaymentRequest(sagaId));
                } catch (DataAccessException e) {
                  log.error(
                      "DataAccessException occurred for thread 2 with sql state: {}",
                      ((PSQLException) Objects.requireNonNull(e.getRootCause())).getSQLState());
                }
              }));

      executor.invokeAll(tasks);

      assertOrderOutbox(sagaId);
    } catch (InterruptedException e) {
      log.error("Error calling complete payment!", e);
    } finally {
      if (executor != null) {
        executor.shutdown();
      }
    }
  }

  private void assertOrderOutbox(String sagaId) {
    Optional<OrderOutboxEntity> orderOutboxEntity =
        orderOutboxJpaRepository.findByTypeAndSagaIdAndPaymentStatusAndOutboxStatus(
            ORDER_SAGA_NAME,
            fromString(sagaId),
            PaymentStatus.COMPLETED,
            OutboxStatus.STARTED);
    assertTrue(orderOutboxEntity.isPresent());
    assertEquals(orderOutboxEntity.get().getSagaId().toString(), sagaId);
  }

  private PaymentRequest getPaymentRequest(String sagaId) {
    return PaymentRequest.builder()
        .id(randomUUID().toString())
        .sagaId(sagaId)
        .orderId(randomUUID().toString())
        .paymentOrderStatus(
            dev.hectorolea.food.ordering.system.domain.valueobject.PaymentOrderStatus.PENDING)
        .customerId(CUSTOMER_ID)
        .price(PRICE)
        .createdAt(now())
        .build();
  }
}
