package dev.hectorolea.food.ordering.system.kafka.producer.exception;

public class KafkaProducerException extends RuntimeException {

  public KafkaProducerException(String message) {
    super(message);
  }
}
