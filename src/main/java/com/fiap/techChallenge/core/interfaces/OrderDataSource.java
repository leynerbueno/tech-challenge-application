package com.fiap.techChallenge.core.interfaces;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.fiap.techChallenge.core.application.dto.order.OrderDTO;
import com.fiap.techChallenge.core.application.dto.order.projection.OrderWithItemsAndStatusProjection;
import com.fiap.techChallenge.core.application.dto.order.projection.OrderWithStatusAndWaitMinutesProjection;
import com.fiap.techChallenge.core.application.dto.order.projection.OrderWithStatusProjection;

public interface OrderDataSource {

    OrderDTO save(OrderDTO order);

    OrderDTO findById(UUID id);

    Optional<OrderWithItemsAndStatusProjection> findWithDetailsById(UUID id);

    List<OrderWithStatusProjection> listByPeriod(LocalDateTime initialDt, LocalDateTime finalDt);

    List<OrderWithStatusAndWaitMinutesProjection> listTodayOrders(List<String> statusList, int finalizedMinutes);

    OrderDTO findByPaymentId(String paymentId);
}
