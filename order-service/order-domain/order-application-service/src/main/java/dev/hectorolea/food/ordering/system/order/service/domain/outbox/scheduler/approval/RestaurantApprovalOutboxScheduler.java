package dev.hectorolea.food.ordering.system.order.service.domain.outbox.scheduler.approval;

import static java.util.stream.Collectors.joining;

import dev.hectorolea.food.ordering.system.order.service.domain.outbox.model.approval.OrderApprovalOutboxMessage;
import dev.hectorolea.food.ordering.system.order.service.domain.ports.output.message.publisher.restaurantapproval.RestaurantApprovalRequestMessagePublisher;
import dev.hectorolea.food.ordering.system.outbox.OutboxScheduler;
import dev.hectorolea.food.ordering.system.outbox.OutboxStatus;
import dev.hectorolea.food.ordering.system.saga.SagaStatus;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
public class RestaurantApprovalOutboxScheduler implements OutboxScheduler {

  private final ApprovalOutboxHelper approvalOutboxHelper;
  private final RestaurantApprovalRequestMessagePublisher restaurantApprovalRequestMessagePublisher;

  public RestaurantApprovalOutboxScheduler(
      ApprovalOutboxHelper approvalOutboxHelper,
      RestaurantApprovalRequestMessagePublisher restaurantApprovalRequestMessagePublisher) {
    this.approvalOutboxHelper = approvalOutboxHelper;
    this.restaurantApprovalRequestMessagePublisher = restaurantApprovalRequestMessagePublisher;
  }

  @Override
  @Transactional
  @Scheduled(
      fixedDelayString = "${order-service.outbox-scheduler-fixed-rate}",
      initialDelayString = "${order-service.outbox-scheduler-initial-delay}")
  public void processOutboxMessage() {
    Optional<List<OrderApprovalOutboxMessage>> outboxMessagesResponse =
        approvalOutboxHelper.getApprovalOutboxMessageByOutboxStatusAndSagaStatus(
            OutboxStatus.STARTED, SagaStatus.PROCESSING);
    if (outboxMessagesResponse.isPresent() && outboxMessagesResponse.get().size() > 0) {
      List<OrderApprovalOutboxMessage> outboxMessages = outboxMessagesResponse.get();
      log.info(
          "Received {} OrderApprovalOutboxMessage with ids: {}, sending to message bus!",
          outboxMessages.size(),
          outboxMessages.stream()
              .map(outboxMessage -> outboxMessage.getId().toString())
              .collect(joining(",")));
      outboxMessages.forEach(
          outboxMessage ->
              restaurantApprovalRequestMessagePublisher.publish(
                  outboxMessage, this::updateOutboxStatus));
      log.info("{} OrderApprovalOutboxMessage sent to message bus!", outboxMessages.size());
    }
  }

  private void updateOutboxStatus(
      OrderApprovalOutboxMessage orderApprovalOutboxMessage, OutboxStatus outboxStatus) {
    orderApprovalOutboxMessage.setOutboxStatus(outboxStatus);
    approvalOutboxHelper.save(orderApprovalOutboxMessage);
    log.info("OrderApprovalOutboxMessage is updated with outbox status: {}", outboxStatus.name());
  }
}
