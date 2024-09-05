package dev.hectorolea.food.ordering.system.order.service.domain;

import static java.util.UUID.fromString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import dev.hectorolea.food.ordering.system.domain.valueobject.CustomerId;
import dev.hectorolea.food.ordering.system.domain.valueobject.Money;
import dev.hectorolea.food.ordering.system.domain.valueobject.OrderId;
import dev.hectorolea.food.ordering.system.domain.valueobject.OrderStatus;
import dev.hectorolea.food.ordering.system.domain.valueobject.ProductId;
import dev.hectorolea.food.ordering.system.domain.valueobject.RestaurantId;
import dev.hectorolea.food.ordering.system.order.service.domain.dto.create.CreateOrderCommand;
import dev.hectorolea.food.ordering.system.order.service.domain.dto.create.CreateOrderResponse;
import dev.hectorolea.food.ordering.system.order.service.domain.dto.create.OrderAddress;
import dev.hectorolea.food.ordering.system.order.service.domain.dto.create.OrderItem;
import dev.hectorolea.food.ordering.system.order.service.domain.entity.Customer;
import dev.hectorolea.food.ordering.system.order.service.domain.entity.Order;
import dev.hectorolea.food.ordering.system.order.service.domain.entity.Product;
import dev.hectorolea.food.ordering.system.order.service.domain.entity.Restaurant;
import dev.hectorolea.food.ordering.system.order.service.domain.exception.OrderDomainException;
import dev.hectorolea.food.ordering.system.order.service.domain.mapper.OrderDataMapper;
import dev.hectorolea.food.ordering.system.order.service.domain.ports.input.service.OrderApplicationService;
import dev.hectorolea.food.ordering.system.order.service.domain.ports.output.repository.CustomerRepository;
import dev.hectorolea.food.ordering.system.order.service.domain.ports.output.repository.OrderRepository;
import dev.hectorolea.food.ordering.system.order.service.domain.ports.output.repository.RestaurantRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(classes = OrderTestConfiguration.class)
public class OrderApplicationServiceTest {

  @Autowired private OrderApplicationService orderApplicationService;

  @Autowired private OrderDataMapper orderDataMapper;

  @Autowired private OrderRepository orderRepository;

  @Autowired private CustomerRepository customerRepository;

  @Autowired private RestaurantRepository restaurantRepository;

  private CreateOrderCommand createOrderCommand;
  private CreateOrderCommand createOrderCommandWrongPrice;
  private CreateOrderCommand createOrderCommandWrongProductPrice;
  private final UUID CUSTOMER_ID = fromString("d215b5f8-0249-4dc5-89a3-51fd148cfb41");
  private final UUID RESTAURANT_ID = fromString("d215b5f8-0249-4dc5-89a3-51fd148cfb45");
  private final UUID PRODUCT_ID = fromString("d215b5f8-0249-4dc5-89a3-51fd148cfb48");
  private final UUID ORDER_ID = fromString("15a497c1-0f4b-4eff-b9f4-c402c8c07afb");
  private final UUID SAGA_ID = fromString("15a497c1-0f4b-4eff-b9f4-c402c8c07afa");
  private final BigDecimal PRICE = new BigDecimal("200.00");

  @BeforeAll
  public void init() {
    createOrderCommand =
        CreateOrderCommand.builder()
            .customerId(CUSTOMER_ID)
            .restaurantId(RESTAURANT_ID)
            .address(
                OrderAddress.builder()
                    .street("street_1")
                    .postalCode("1000AB")
                    .city("Paris")
                    .build())
            .price(PRICE)
            .items(
                List.of(
                    OrderItem.builder()
                        .productId(PRODUCT_ID)
                        .quantity(1)
                        .price(new BigDecimal("50.00"))
                        .subTotal(new BigDecimal("50.00"))
                        .build(),
                    OrderItem.builder()
                        .productId(PRODUCT_ID)
                        .quantity(3)
                        .price(new BigDecimal("50.00"))
                        .subTotal(new BigDecimal("150.00"))
                        .build()))
            .build();

    createOrderCommandWrongPrice =
        CreateOrderCommand.builder()
            .customerId(CUSTOMER_ID)
            .restaurantId(RESTAURANT_ID)
            .address(
                OrderAddress.builder()
                    .street("street_1")
                    .postalCode("1000AB")
                    .city("Paris")
                    .build())
            .price(new BigDecimal("250.00"))
            .items(
                List.of(
                    OrderItem.builder()
                        .productId(PRODUCT_ID)
                        .quantity(1)
                        .price(new BigDecimal("50.00"))
                        .subTotal(new BigDecimal("50.00"))
                        .build(),
                    OrderItem.builder()
                        .productId(PRODUCT_ID)
                        .quantity(3)
                        .price(new BigDecimal("50.00"))
                        .subTotal(new BigDecimal("150.00"))
                        .build()))
            .build();

    createOrderCommandWrongProductPrice =
        CreateOrderCommand.builder()
            .customerId(CUSTOMER_ID)
            .restaurantId(RESTAURANT_ID)
            .address(
                OrderAddress.builder()
                    .street("street_1")
                    .postalCode("1000AB")
                    .city("Paris")
                    .build())
            .price(new BigDecimal("210.00"))
            .items(
                List.of(
                    OrderItem.builder()
                        .productId(PRODUCT_ID)
                        .quantity(1)
                        .price(new BigDecimal("60.00"))
                        .subTotal(new BigDecimal("60.00"))
                        .build(),
                    OrderItem.builder()
                        .productId(PRODUCT_ID)
                        .quantity(3)
                        .price(new BigDecimal("50.00"))
                        .subTotal(new BigDecimal("150.00"))
                        .build()))
            .build();

    Customer customer = new Customer(new CustomerId(CUSTOMER_ID));

    Restaurant restaurantResponse =
        Restaurant.builder()
            .restaurantId(new RestaurantId(createOrderCommand.getRestaurantId()))
            .products(
                List.of(
                    new Product(
                        new ProductId(PRODUCT_ID), "product-1", new Money(new BigDecimal("50.00"))),
                    new Product(
                        new ProductId(PRODUCT_ID),
                        "product-2",
                        new Money(new BigDecimal("50.00")))))
            .isActive(true)
            .build();

    Order order = orderDataMapper.createOrderCommandToOrder(createOrderCommand);
    order.setId(new OrderId(ORDER_ID));

    when(customerRepository.findCustomer(CUSTOMER_ID)).thenReturn(Optional.of(customer));
    when(restaurantRepository.findRestaurantInformation(
            orderDataMapper.createOrderCommandToRestaurant(createOrderCommand)))
        .thenReturn(Optional.of(restaurantResponse));
    when(orderRepository.save(any(Order.class))).thenReturn(order);
  }

  @Test
  public void testCreateOrder() {
    CreateOrderResponse createOrderResponse =
        orderApplicationService.createOrder(createOrderCommand);
    assertEquals(OrderStatus.PENDING, createOrderResponse.getOrderStatus());
    assertEquals("Order created successfully", createOrderResponse.getMessage());
    Assertions.assertNotNull(createOrderResponse.getOrderTackingId());
  }

  @Test
  public void testCreateOrderWithWrongTotalPrice() {
    OrderDomainException orderDomainException =
        assertThrows(
            OrderDomainException.class,
            () -> orderApplicationService.createOrder(createOrderCommandWrongPrice));
    assertEquals(
        "Total price: 250.00 is not equal to Order items total: 200.00!",
        orderDomainException.getMessage());
  }

  @Test
  public void testCreateOrderWithWrongProductPrice() {
    OrderDomainException orderDomainException =
        assertThrows(
            OrderDomainException.class,
            () -> orderApplicationService.createOrder(createOrderCommandWrongProductPrice));
    assertEquals(
        "Order item price: 60.00 is not valid for product " + PRODUCT_ID,
        orderDomainException.getMessage());
  }

  @Test
  public void testCreateOrderWithPassiveRestaurant() {
    Restaurant restaurantResponse =
        Restaurant.builder()
            .restaurantId(new RestaurantId(createOrderCommand.getRestaurantId()))
            .products(
                List.of(
                    new Product(
                        new ProductId(PRODUCT_ID), "product-1", new Money(new BigDecimal("50.00"))),
                    new Product(
                        new ProductId(PRODUCT_ID),
                        "product-2",
                        new Money(new BigDecimal("50.00")))))
            .isActive(false)
            .build();
    when(restaurantRepository.findRestaurantInformation(
            orderDataMapper.createOrderCommandToRestaurant(createOrderCommand)))
        .thenReturn(Optional.of(restaurantResponse));
    OrderDomainException orderDomainException =
        assertThrows(
            OrderDomainException.class,
            () -> orderApplicationService.createOrder(createOrderCommand));
    assertEquals(
        "Restaurant with id " + RESTAURANT_ID + " is currently not active!",
        orderDomainException.getMessage());
  }
}
