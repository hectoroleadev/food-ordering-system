package dev.hectorolea.food.ordering.system.restaurant.service.domain;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfiguration {

  @Bean
  public RestaurantDomainService restaurantDomainService() {
    return new RestaurantDomainServiceImpl();
  }
}
