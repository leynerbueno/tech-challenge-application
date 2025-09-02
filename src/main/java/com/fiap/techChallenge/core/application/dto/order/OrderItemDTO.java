package com.fiap.techChallenge.core.application.dto.order;

import java.math.BigDecimal;
import java.util.UUID;

import com.fiap.techChallenge.core.domain.enums.Category;

import lombok.Builder;

@Builder
public record OrderItemDTO(
        UUID productId,
        String productName,
        Integer quantity,
        BigDecimal unitPrice,
        Category category
        ) {

}
