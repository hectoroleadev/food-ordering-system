package dev.hectorolea.food.ordering.system.payment.service.domain.outbox.model;

import dev.hectorolea.food.ordering.system.domain.valueobject.PaymentStatus;
import dev.hectorolea.food.ordering.system.outbox.OutboxStatus;
import java.time.ZonedDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class OrderOutboxMessage {
  private UUID id;
  private UUID sagaId;
  private ZonedDateTime createdAt;
  private ZonedDateTime processedAt;
  private String type;
  private String payload;
  private PaymentStatus paymentStatus;
  private OutboxStatus outboxStatus;
  private int version;

  public void setOutboxStatus(OutboxStatus outboxStatus) {
    this.outboxStatus = outboxStatus;
  }
}
