package com.fiap.techChallenge.core.gateways.payment;

import java.util.UUID;

import com.fiap.techChallenge._webApi.mappers.OrderMapper;
import com.fiap.techChallenge.core.application.dto.order.OrderDTO;
import com.fiap.techChallenge.core.application.dto.payment.PaymentRequestDTO;
import com.fiap.techChallenge.core.application.dto.payment.PaymentResponseDTO;
import com.fiap.techChallenge.core.domain.entities.order.Order;
import com.fiap.techChallenge.core.domain.enums.PaymentStatus;
import com.fiap.techChallenge.core.interfaces.CompositeDataSource;

public class PaymentGatewayImpl implements PaymentGateway {

    private final CompositeDataSource compositeDataSource;

    public  PaymentGatewayImpl(CompositeDataSource compositeDataSource) {
        this.compositeDataSource = compositeDataSource;
    }

    @Override
    public PaymentResponseDTO process(PaymentRequestDTO request) {
        OrderDTO orderDto = compositeDataSource.findOrderById(request.getOrderId());

        if (orderDto == null) {
            throw new IllegalArgumentException("Order não encontrada ou inválida.");
        }

        Order order =  OrderMapper.toDomain(orderDto);

        return compositeDataSource.processPayment(request, order);
   }

    @Override
    public UUID findApprovedOrderByPaymentId(String paymentId) {
        return compositeDataSource.findApprovedOrderByPaymentId(paymentId);
    }

    @Override
    public String findPaymentByOrderId(UUID orderId) {
        return compositeDataSource.findIdPayment(orderId);
    }

    @Override
    public PaymentStatus checkStatus(UUID orderId) {
        return compositeDataSource.checkStatus(orderId);
    }
}
