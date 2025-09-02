package com.fiap.techChallenge.core.application.useCases.order;

import java.util.UUID;

import com.fiap.techChallenge.core.domain.exceptions.EntityNotFoundException;
import com.fiap.techChallenge.core.application.dto.order.OrderWithItemsAndStatusDTO;
import com.fiap.techChallenge.core.gateways.order.OrderGateway;

public class FindOrderByIdUseCase {

    private final OrderGateway orderGateway;

    public FindOrderByIdUseCase(OrderGateway orderGateway) {
        this.orderGateway = orderGateway;
    }

    public OrderWithItemsAndStatusDTO execute(UUID id) {
        OrderWithItemsAndStatusDTO orderDTO = orderGateway.findWithDetailsById(id)
                .orElseThrow(() -> new EntityNotFoundException("Order"));

        return orderDTO;
    }

}
