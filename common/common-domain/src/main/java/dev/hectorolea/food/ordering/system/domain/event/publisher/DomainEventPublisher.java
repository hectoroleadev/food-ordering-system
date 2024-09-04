package dev.hectorolea.food.ordering.system.domain.event.publisher;

import dev.hectorolea.food.ordering.system.domain.event.DomainEvent;

public interface DomainEventPublisher<T extends DomainEvent> {
  void publish(T domainEvent);
}
