package com.fiap.techChallenge.core.interfaces;

import java.util.UUID;

import com.fiap.techChallenge.core.application.dto.payment.PaymentRequestDTO;
import com.fiap.techChallenge.core.application.dto.payment.PaymentResponseDTO;
import com.fiap.techChallenge.core.domain.entities.order.Order;
import com.fiap.techChallenge.core.domain.enums.PaymentStatus;

public interface MercadoPagoSource {

    PaymentResponseDTO processPayment(PaymentRequestDTO request, Order order);

    String findIdPayment(UUID paymentId);

    PaymentStatus checkStatus(UUID orderId);

    UUID findApprovedOrderByPaymentId(String paymentId);
}
