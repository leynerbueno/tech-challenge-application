package com.fiap.techChallenge.core.application.dto.order;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.fiap.techChallenge.core.domain.entities.order.OrderItem;
import com.fiap.techChallenge.core.domain.entities.order.OrderStatusHistory;
import com.fiap.techChallenge.core.domain.entities.user.customer.Customer;


import lombok.Builder;

@Builder
public record OrderResponseDTO(
        UUID id,
        List<OrderItem> items,
        List<OrderStatusHistory> statusHistory,
        Customer customer,
        BigDecimal price,
        LocalDateTime date,
        String paymentId
        ) {

}
