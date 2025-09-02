package com.fiap.techChallenge.core.application.useCases.order;

import java.util.List;

import com.fiap.techChallenge.core.application.dto.order.OrderWithStatusAndWaitMinutesDTO;
import com.fiap.techChallenge.core.domain.enums.OrderStatus;
import com.fiap.techChallenge.core.gateways.order.OrderGateway;

public class ListTodayOrdersUseCase {

    private final OrderGateway orderGateway;

    public ListTodayOrdersUseCase(OrderGateway orderGateway) {
        this.orderGateway = orderGateway;
    }

    public List<OrderWithStatusAndWaitMinutesDTO> execute() {
        List<String> statusList = List.of(
                OrderStatus.RECEBIDO.name(),
                OrderStatus.EM_PREPARACAO.name(),
                OrderStatus.PRONTO.name()
        );

        return orderGateway.listTodayOrders(statusList, 5);
    }
}
