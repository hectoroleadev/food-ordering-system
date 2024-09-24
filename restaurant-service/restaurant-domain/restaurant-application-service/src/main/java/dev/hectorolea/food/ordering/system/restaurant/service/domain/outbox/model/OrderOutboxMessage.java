package dev.hectorolea.food.ordering.system.restaurant.service.domain.outbox.model;

import dev.hectorolea.food.ordering.system.domain.valueobject.OrderApprovalStatus;
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
  private OutboxStatus outboxStatus;
  private OrderApprovalStatus approvalStatus;
  private int version;

  public void setOutboxStatus(OutboxStatus status) {
    this.outboxStatus = status;
  }
}
