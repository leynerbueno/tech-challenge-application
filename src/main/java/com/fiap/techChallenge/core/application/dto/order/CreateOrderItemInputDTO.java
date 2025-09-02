package com.fiap.techChallenge.core.application.dto.order;

import java.util.UUID;

import lombok.Builder;

@Builder
public record CreateOrderItemInputDTO(
        UUID productId,
        Integer quantity
        ) {

}
