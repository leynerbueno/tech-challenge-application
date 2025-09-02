package com.fiap.techChallenge._webApi.dto.order;

import java.util.UUID;

import lombok.Builder;

@Builder
public record CreateOrderItemDTO(
        UUID productId,
        Integer quantity
        ) {

}
