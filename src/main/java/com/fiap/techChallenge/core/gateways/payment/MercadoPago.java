package com.fiap.techChallenge.core.gateways.payment;

import com.fiap.techChallenge.core.application.dto.payment.PaymentRequestDTO;
import com.fiap.techChallenge.core.application.dto.payment.PaymentResponseDTO;
import com.fiap.techChallenge.core.domain.entities.order.Order;


import java.util.UUID;

public interface MercadoPago {

    PaymentResponseDTO processPayment(PaymentRequestDTO request, Order order);

    String findByIdPayment(UUID paymentId);
}
