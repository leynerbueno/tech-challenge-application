package com.fiap.techChallenge.core.application.useCases.order;

import java.util.UUID;

import com.fiap.techChallenge.core.domain.exceptions.EntityNotFoundException;
import com.fiap.techChallenge.core.domain.entities.order.Order;
import com.fiap.techChallenge.core.domain.entities.product.Product;
import com.fiap.techChallenge.core.gateways.order.OrderGateway;
import com.fiap.techChallenge.core.gateways.product.ProductGateway;

public class RemoveItemFromOrderUseCase {

    private final OrderGateway orderGateway;
    private final ProductGateway productGateway;

    public RemoveItemFromOrderUseCase(OrderGateway orderGateway, ProductGateway productGateway) {
        this.orderGateway = orderGateway;
        this.productGateway = productGateway;
    }

    public Order execute(UUID orderId, UUID productId, int quantity) {
        Order order = findOrder(orderId);
        this.validateIfProductExists(productId);

        order.removeItem(productId, quantity);
        return orderGateway.save(order);
    }

    private Order findOrder(UUID orderId) {
        Order order = orderGateway.findById(orderId);

        if (order == null) {
            throw new EntityNotFoundException("Order");
        }

        return order;
    }

    private void validateIfProductExists(UUID productId) {
        Product product = productGateway.findById(productId);

        if (product == null || product.getId() == null) {
            throw new EntityNotFoundException("Product");
        }
    }

}
