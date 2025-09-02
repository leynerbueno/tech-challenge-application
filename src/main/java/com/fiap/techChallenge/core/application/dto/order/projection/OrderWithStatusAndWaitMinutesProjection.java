package com.fiap.techChallenge.core.application.dto.order.projection;

import java.time.LocalDateTime;
import java.util.UUID;

import com.fiap.techChallenge.core.domain.enums.OrderStatus;

public interface OrderWithStatusAndWaitMinutesProjection {

    UUID getOrderId();

    OrderStatus getStatus();

    LocalDateTime getStatusDt();

    UUID getCustomerId();

    String getCustomerName();

    UUID getAttendantId();

    String getAttendantName();

    LocalDateTime getOrderDt();

    int getWaitTimeMinutes();
}
