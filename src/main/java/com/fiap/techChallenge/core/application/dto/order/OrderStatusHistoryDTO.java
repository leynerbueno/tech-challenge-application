package com.fiap.techChallenge.core.application.dto.order;

import java.time.LocalDateTime;

import com.fiap.techChallenge.core.application.dto.user.AttendantDTO;
import com.fiap.techChallenge.core.domain.enums.OrderStatus;

import lombok.Builder;

@Builder
public record OrderStatusHistoryDTO(
        AttendantDTO attendant,
        OrderStatus status,
        LocalDateTime date
        ) {

}
