package dev.hectorolea.food.ordering.system.order.service.dataaccess.order.entity;

import static java.util.Objects.hash;

import dev.hectorolea.food.ordering.system.domain.valueobject.OrderStatus;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import javax.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "orders")
@Entity
public class OrderEntity {
  @Id private UUID id;
  private UUID customerId;
  private UUID restaurantId;
  private UUID trackingId;
  private BigDecimal price;

  @Enumerated(EnumType.STRING)
  private OrderStatus orderStatus;

  private String failureMessages;

  @OneToOne(mappedBy = "order", cascade = CascadeType.ALL)
  private OrderAddressEntity address;

  @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
  private List<OrderItemEntity> items;

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    OrderEntity that = (OrderEntity) o;
    return id.equals(that.id);
  }

  @Override
  public int hashCode() {
    return hash(id);
  }
}
