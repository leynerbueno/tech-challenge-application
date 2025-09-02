package com.fiap.techChallenge.core.application.dto.order;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.fiap.techChallenge.core.application.dto.user.CustomerFullDTO;

import lombok.Builder;

@Builder
public record OrderDTO(
        UUID id,
        List<OrderItemDTO> items,
        List<OrderStatusHistoryDTO> statusHistory,
        CustomerFullDTO customer,
        BigDecimal price,
        LocalDateTime date,
        String paymentId
        ) {

}
