package com.fiap.techChallenge.core.gateways.order;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.fiap.techChallenge.core.application.dto.order.OrderWithItemsAndStatusDTO;
import com.fiap.techChallenge.core.application.dto.order.OrderWithStatusAndWaitMinutesDTO;
import com.fiap.techChallenge.core.application.dto.order.OrderWithStatusDTO;
import com.fiap.techChallenge.core.domain.entities.order.Order;

public interface OrderGateway {

    Order save(Order order);

    Order findById(UUID id);

    Optional<OrderWithItemsAndStatusDTO> findWithDetailsById(UUID id);

    List<OrderWithStatusDTO> listByPeriod(LocalDateTime initialDt, LocalDateTime finalDt);

    List<OrderWithStatusAndWaitMinutesDTO> listTodayOrders(List<String> statusList, int finalizedMinutes);

    void updateOrderIdPayment(UUID orderId, String paymentId);

    Order findByPaymentId(String paymentId);
}
