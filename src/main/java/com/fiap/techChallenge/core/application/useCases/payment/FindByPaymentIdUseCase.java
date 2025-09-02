package com.fiap.techChallenge.core.application.useCases.payment;

import java.util.UUID;

import com.fiap.techChallenge.core.gateways.payment.PaymentGateway;

public class FindByPaymentIdUseCase {

    private final PaymentGateway paymentGateway;

    public FindByPaymentIdUseCase(PaymentGateway paymentGateway) {
        this.paymentGateway = paymentGateway;
    }

    public String execute(UUID orderId) {
        String idApplication = paymentGateway.findPaymentByOrderId(orderId);
        return idApplication;
    }
}
