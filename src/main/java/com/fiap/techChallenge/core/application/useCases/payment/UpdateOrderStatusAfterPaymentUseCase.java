package com.fiap.techChallenge.core.application.useCases.payment;

import java.util.UUID;

import com.fiap.techChallenge.core.application.services.order.OrderStatusUpdaterService;
import com.fiap.techChallenge.core.gateways.order.OrderGateway;
import com.fiap.techChallenge.core.gateways.payment.PaymentGateway;

public class UpdateOrderStatusAfterPaymentUseCase {

    private final PaymentGateway paymentGateway;
    private final OrderGateway orderGateway;
    private final OrderStatusUpdaterService orderStatusUpdaterService;

    public UpdateOrderStatusAfterPaymentUseCase(PaymentGateway paymentGateway, OrderGateway orderGateway, OrderStatusUpdaterService orderStatusUpdaterService) {
        this.paymentGateway = paymentGateway;
        this.orderGateway = orderGateway;
        this.orderStatusUpdaterService = orderStatusUpdaterService;
    }

    public void execute(String paymentId) {
        UUID orderId = paymentGateway.findApprovedOrderByPaymentId(paymentId);
        // SÓ NÃO INSERIU P PAYMENT ID NA ORDER
        if (orderId == null) {
            throw new IllegalArgumentException("Nenhum pedido aprovado encontrado para o ID de pagamento: " + paymentId);
        }

        orderGateway.updateOrderIdPayment(orderId, paymentId);
        orderStatusUpdaterService.moveStatusToPaid(orderId);
    }
}
