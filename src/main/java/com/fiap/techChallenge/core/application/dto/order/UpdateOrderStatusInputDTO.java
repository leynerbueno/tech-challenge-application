package com.fiap.techChallenge.core.application.dto.order;

import java.util.UUID;

import com.fiap.techChallenge.core.domain.enums.OrderStatus;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record UpdateOrderStatusInputDTO(
        @NotNull
        UUID orderId,
        @NotNull
        OrderStatus status,
        UUID attendantId
        ) {

}
