package com.fiap.techChallenge.core.gateways.payment;

import com.fiap.techChallenge.core.application.dto.payment.PaymentResponseDTO;
import com.fiap.techChallenge.core.application.dto.payment.PaymentRequestDTO;
import com.fiap.techChallenge.core.domain.enums.PaymentStatus;

import java.util.UUID;

public interface PaymentGateway {

    PaymentResponseDTO process(PaymentRequestDTO request);

    String findPaymentByOrderId(UUID orderId);

    PaymentStatus checkStatus(UUID orderId);

    UUID findApprovedOrderByPaymentId(String paymentId);
}
