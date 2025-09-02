package com.fiap.techChallenge.core.application.dto.order;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import com.fiap.techChallenge.core.domain.enums.OrderStatus;

import lombok.Builder;

@Builder
public record OrderSummaryDTO(
        UUID id,
        OrderStatus status,
        LocalDateTime statusDt,
        UUID customerId,
        String customerName,
        UUID attendantId,
        String attendantName,
        BigDecimal price,
        LocalDateTime orderDt
        ) {

}
