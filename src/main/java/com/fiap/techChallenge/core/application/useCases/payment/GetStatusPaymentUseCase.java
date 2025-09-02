package com.fiap.techChallenge.core.application.useCases.payment;

import com.fiap.techChallenge.core.domain.enums.PaymentStatus;
import com.fiap.techChallenge.core.gateways.payment.PaymentGateway;

import java.util.UUID;

public class GetStatusPaymentUseCase {

    private final PaymentGateway paymentGateway;
    public GetStatusPaymentUseCase(PaymentGateway paymentGateway) {
        this.paymentGateway = paymentGateway;
    }

    public PaymentStatus execute(UUID orderId) {
        return paymentGateway.checkStatus(orderId);
    }


}
