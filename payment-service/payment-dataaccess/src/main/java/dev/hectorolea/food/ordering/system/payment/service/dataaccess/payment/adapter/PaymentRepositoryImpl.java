package dev.hectorolea.food.ordering.system.payment.service.dataaccess.payment.adapter;

import dev.hectorolea.food.ordering.system.payment.service.dataaccess.payment.mapper.PaymentDataAccessMapper;
import dev.hectorolea.food.ordering.system.payment.service.dataaccess.payment.repository.PaymentJpaRepository;
import dev.hectorolea.food.ordering.system.payment.service.domain.entity.Payment;
import dev.hectorolea.food.ordering.system.payment.service.domain.ports.output.repository.PaymentRepository;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class PaymentRepositoryImpl implements PaymentRepository {

  private final PaymentJpaRepository paymentJpaRepository;
  private final PaymentDataAccessMapper paymentDataAccessMapper;

  public PaymentRepositoryImpl(
      PaymentJpaRepository paymentJpaRepository, PaymentDataAccessMapper paymentDataAccessMapper) {
    this.paymentJpaRepository = paymentJpaRepository;
    this.paymentDataAccessMapper = paymentDataAccessMapper;
  }

  @Override
  public Payment save(Payment payment) {
    return paymentDataAccessMapper.paymentEntityToPayment(
        paymentJpaRepository.save(paymentDataAccessMapper.paymentToPaymentEntity(payment)));
  }

  @Override
  public Optional<Payment> findByOrderId(UUID orderId) {
    return paymentJpaRepository
        .findByOrderId(orderId)
        .map(paymentDataAccessMapper::paymentEntityToPayment);
  }
}
