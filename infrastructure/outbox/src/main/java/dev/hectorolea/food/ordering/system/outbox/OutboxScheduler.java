package dev.hectorolea.food.ordering.system.outbox;

public interface OutboxScheduler {
  void processOutboxMessage();
}
