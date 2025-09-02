package com.fiap.techChallenge.core.application.useCases.order;

import java.util.UUID;

import com.fiap.techChallenge.core.domain.exceptions.EntityNotFoundException;
import com.fiap.techChallenge.core.application.services.product.ProductAvailabilityService;
import com.fiap.techChallenge.core.domain.entities.order.Order;
import com.fiap.techChallenge.core.domain.entities.product.Product;
import com.fiap.techChallenge.core.gateways.order.OrderGateway;

public class AddItemInOrderUseCase {

    public final OrderGateway orderGateway;
    public final ProductAvailabilityService productAvailabilityService;

    public AddItemInOrderUseCase(OrderGateway orderGateway, ProductAvailabilityService productAvailabilityService) {
        this.orderGateway = orderGateway;
        this.productAvailabilityService = productAvailabilityService;
    }

    public Order execute(UUID orderId, UUID productId, int quantity) {
        Order order = orderGateway.findById(orderId);

        if (order == null) {
            throw new EntityNotFoundException("Order");
        }

        Product product = productAvailabilityService.findAvailableProduct(productId);

        order.addItem(product, quantity);
        return orderGateway.save(order);
    }
}
