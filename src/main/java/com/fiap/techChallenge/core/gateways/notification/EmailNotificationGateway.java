package com.fiap.techChallenge.core.gateways.notification;


import java.util.UUID;

import com.fiap.techChallenge.core.domain.enums.OrderStatus;

public interface EmailNotificationGateway {
    void sendEmail(String toEmail, UUID orderId, OrderStatus status);
}
