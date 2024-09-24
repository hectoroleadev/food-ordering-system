package dev.hectorolea.food.ordering.system.restaurant.service.dataaccess.restaurant.outbox.entity;

import static java.util.Objects.hash;

import dev.hectorolea.food.ordering.system.domain.valueobject.OrderApprovalStatus;
import dev.hectorolea.food.ordering.system.outbox.OutboxStatus;
import java.time.ZonedDateTime;
import java.util.UUID;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "order_outbox")
@Entity
public class OrderOutboxEntity {

  @Id private UUID id;
  private UUID sagaId;
  private ZonedDateTime createdAt;
  private ZonedDateTime processedAt;
  private String type;
  private String payload;

  @Enumerated(EnumType.STRING)
  private OutboxStatus outboxStatus;

  @Enumerated(EnumType.STRING)
  private OrderApprovalStatus approvalStatus;

  private int version;

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    OrderOutboxEntity that = (OrderOutboxEntity) o;
    return id.equals(that.id);
  }

  @Override
  public int hashCode() {
    return hash(id);
  }
}
