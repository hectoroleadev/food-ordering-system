package dev.hectorolea.food.ordering.system.order.service.domain;

import static org.mockito.Mockito.mock;

import dev.hectorolea.food.ordering.system.order.service.domain.ports.output.message.publisher.payment.PaymentRequestMessagePublisher;
import dev.hectorolea.food.ordering.system.order.service.domain.ports.output.message.publisher.restaurantapproval.RestaurantApprovalRequestMessagePublisher;
import dev.hectorolea.food.ordering.system.order.service.domain.ports.output.repository.ApprovalOutboxRepository;
import dev.hectorolea.food.ordering.system.order.service.domain.ports.output.repository.CustomerRepository;
import dev.hectorolea.food.ordering.system.order.service.domain.ports.output.repository.OrderRepository;
import dev.hectorolea.food.ordering.system.order.service.domain.ports.output.repository.PaymentOutboxRepository;
import dev.hectorolea.food.ordering.system.order.service.domain.ports.output.repository.RestaurantRepository;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication(scanBasePackages = "dev.hectorolea.food.ordering.system")
public class OrderTestConfiguration {
  @Bean
  public PaymentRequestMessagePublisher paymentRequestMessagePublisher() {
    return mock(PaymentRequestMessagePublisher.class);
  }

  @Bean
  public RestaurantApprovalRequestMessagePublisher restaurantApprovalRequestMessagePublisher() {
    return mock(RestaurantApprovalRequestMessagePublisher.class);
  }

  @Bean
  public OrderRepository orderRepository() {
    return mock(OrderRepository.class);
  }

  @Bean
  public CustomerRepository customerRepository() {
    return mock(CustomerRepository.class);
  }

  @Bean
  public RestaurantRepository restaurantRepository() {
    return mock(RestaurantRepository.class);
  }

  @Bean
  public PaymentOutboxRepository paymentOutboxRepository() {
    return mock(PaymentOutboxRepository.class);
  }

  @Bean
  public ApprovalOutboxRepository approvalOutboxRepository() {
    return mock(ApprovalOutboxRepository.class);
  }

  @Bean
  public OrderDomainService orderDomainService() {
    return new OrderDomainServiceImpl();
  }
}
