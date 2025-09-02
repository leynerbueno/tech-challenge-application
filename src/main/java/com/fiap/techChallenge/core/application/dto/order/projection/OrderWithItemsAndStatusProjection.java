package com.fiap.techChallenge.core.application.dto.order.projection;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import com.fiap.techChallenge.core.domain.enums.OrderStatus;

public interface OrderWithItemsAndStatusProjection {
    UUID getOrderId();
    OrderStatus getStatus();
    LocalDateTime getStatusDt();
    UUID getCustomerId();
    String getCustomerName();
    UUID getAttendantId();
    String getAttendantName();
    BigDecimal getPrice();
    LocalDateTime getOrderDt();
    String getPaymentId();
    String getItemsJson(); 
}