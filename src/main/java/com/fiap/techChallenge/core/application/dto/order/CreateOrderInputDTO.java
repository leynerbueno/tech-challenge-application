package com.fiap.techChallenge.core.application.dto.order;

import java.util.List;
import java.util.UUID;


import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record CreateOrderInputDTO(
        @NotNull
        List<CreateOrderItemInputDTO> items,
        @NotNull
        UUID customerId
        ) {

}
