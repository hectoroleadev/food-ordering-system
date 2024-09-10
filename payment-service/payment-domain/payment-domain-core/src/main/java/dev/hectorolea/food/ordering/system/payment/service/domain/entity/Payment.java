package dev.hectorolea.food.ordering.system.payment.service.domain.entity;

import static dev.hectorolea.food.ordering.system.domain.DomainConstants.UTC;
import static java.time.ZoneId.of;
import static java.time.ZonedDateTime.now;
import static java.util.UUID.randomUUID;

import dev.hectorolea.food.ordering.system.domain.entity.AggregateRoot;
import dev.hectorolea.food.ordering.system.domain.valueobject.CustomerId;
import dev.hectorolea.food.ordering.system.domain.valueobject.Money;
import dev.hectorolea.food.ordering.system.domain.valueobject.OrderId;
import dev.hectorolea.food.ordering.system.domain.valueobject.PaymentStatus;
import dev.hectorolea.food.ordering.system.payment.service.domain.valueobject.PaymentId;
import java.time.ZonedDateTime;
import java.util.List;

public class Payment extends AggregateRoot<PaymentId> {

  private final OrderId orderId;
  private final CustomerId customerId;
  private final Money price;

  private PaymentStatus paymentStatus;
  private ZonedDateTime createdAt;

  public void initializePayment() {
    setId(new PaymentId(randomUUID()));
    createdAt = now(of(UTC));
  }

  public void validatePayment(List<String> failureMessages) {
    if (price == null || !price.isGreaterThanZero()) {
      failureMessages.add("Total price must be greater than zero!");
    }
  }

  public void updateStatus(PaymentStatus paymentStatus) {
    this.paymentStatus = paymentStatus;
  }

  private Payment(Builder builder) {
    setId(builder.paymentId);
    orderId = builder.orderId;
    customerId = builder.customerId;
    price = builder.price;
    paymentStatus = builder.paymentStatus;
    createdAt = builder.createdAt;
  }

  public static Builder builder() {
    return new Builder();
  }

  public OrderId getOrderId() {
    return orderId;
  }

  public CustomerId getCustomerId() {
    return customerId;
  }

  public Money getPrice() {
    return price;
  }

  public PaymentStatus getPaymentStatus() {
    return paymentStatus;
  }

  public ZonedDateTime getCreatedAt() {
    return createdAt;
  }

  public static final class Builder {
    private PaymentId paymentId;
    private OrderId orderId;
    private CustomerId customerId;
    private Money price;
    private PaymentStatus paymentStatus;
    private ZonedDateTime createdAt;

    private Builder() {}

    public Builder paymentId(PaymentId val) {
      paymentId = val;
      return this;
    }

    public Builder orderId(OrderId val) {
      orderId = val;
      return this;
    }

    public Builder customerId(CustomerId val) {
      customerId = val;
      return this;
    }

    public Builder price(Money val) {
      price = val;
      return this;
    }

    public Builder paymentStatus(PaymentStatus val) {
      paymentStatus = val;
      return this;
    }

    public Builder createdAt(ZonedDateTime val) {
      createdAt = val;
      return this;
    }

    public Payment build() {
      return new Payment(this);
    }
  }
}
