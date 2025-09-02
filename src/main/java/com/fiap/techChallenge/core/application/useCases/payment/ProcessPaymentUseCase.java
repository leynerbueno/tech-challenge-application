package com.fiap.techChallenge.core.application.useCases.payment;

import com.fiap.techChallenge.core.application.dto.payment.PaymentRequestDTO;
import com.fiap.techChallenge.core.application.dto.payment.PaymentResponseDTO;
import com.fiap.techChallenge.core.gateways.payment.PaymentGateway;

public class ProcessPaymentUseCase {

    private final PaymentGateway paymentGateway;

    public ProcessPaymentUseCase(PaymentGateway paymentGateway) {
        this.paymentGateway = paymentGateway;
    }

    public PaymentResponseDTO execute(PaymentRequestDTO request) {
        PaymentResponseDTO response = paymentGateway.process(request);
        return response;
    }
}
