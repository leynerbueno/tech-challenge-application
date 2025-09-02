package com.fiap.techChallenge.core.application.useCases.order;

import java.time.LocalDateTime;
import java.util.List;

import com.fiap.techChallenge.core.application.dto.order.OrderWithStatusDTO;
import com.fiap.techChallenge.core.gateways.order.OrderGateway;

public class ListOrdersByPeriodUseCase {

    private final OrderGateway orderGateway;

    public ListOrdersByPeriodUseCase(OrderGateway orderGateway) {
        this.orderGateway = orderGateway;
    }

    public List<OrderWithStatusDTO> execute(LocalDateTime initialDt, LocalDateTime finalDt) {
        return orderGateway.listByPeriod(initialDt, finalDt);
    }
}
