package com.fiap.techChallenge.core.application.useCases.order;

import com.fiap.techChallenge.core.gateways.order.OrderGateway;

import java.util.UUID;

public class UpdateOrderIdPaymentUseCase {

    private final OrderGateway orderGateway;

    public UpdateOrderIdPaymentUseCase(OrderGateway orderGateway) {
        this.orderGateway = orderGateway;
    }

    public void execute(UUID orderId, String paymentId) {
         orderGateway.updateOrderIdPayment(orderId,paymentId );
    }
}
