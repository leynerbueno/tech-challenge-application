package com.fiap.techChallenge.core.controller.payment;

import java.util.UUID;

import com.fiap.techChallenge.core.application.dto.payment.PaymentRequestDTO;
import com.fiap.techChallenge.core.application.dto.payment.PaymentResponseDTO;
import com.fiap.techChallenge.core.application.useCases.payment.FindByPaymentIdUseCase;
import com.fiap.techChallenge.core.application.useCases.payment.GetStatusPaymentUseCase;
import com.fiap.techChallenge.core.application.useCases.payment.ProcessPaymentUseCase;
import com.fiap.techChallenge.core.domain.enums.PaymentStatus;
import com.fiap.techChallenge.core.gateways.payment.PaymentGateway;
import com.fiap.techChallenge.core.gateways.payment.PaymentGatewayImpl;
import com.fiap.techChallenge.core.interfaces.CompositeDataSource;

public class PaymentController {

    private final PaymentGateway paymentGateway;

    public PaymentController(CompositeDataSource compositeDataSource) {
        this.paymentGateway = new PaymentGatewayImpl(compositeDataSource);
    }

    public static PaymentController build(CompositeDataSource compositeDataSource) {
        return new PaymentController(compositeDataSource);
    }

    public PaymentResponseDTO processPayment(PaymentRequestDTO request) {
        ProcessPaymentUseCase processPaymentUseCase = new ProcessPaymentUseCase(paymentGateway);
        return processPaymentUseCase.execute(request);
    }

    public String findPaymentByOrderId(UUID orderId) {
        FindByPaymentIdUseCase findByPaymentIdUseCase = new FindByPaymentIdUseCase(paymentGateway);
        return findByPaymentIdUseCase.execute(orderId);
    }

    public PaymentStatus getStatus(UUID orderId) {
        GetStatusPaymentUseCase getStatusPaymentUseCase = new GetStatusPaymentUseCase(paymentGateway);
        return getStatusPaymentUseCase.execute(orderId);
    }

}
