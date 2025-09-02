package com.fiap.techChallenge.core.application.useCases.notification;

import com.fiap.techChallenge.core.gateways.notification.EmailNotificationGateway;

import java.util.UUID;

import com.fiap.techChallenge.core.domain.enums.OrderStatus;

public class NotificationUseCase {


    private final EmailNotificationGateway emailNotificationGateway;

    public NotificationUseCase(EmailNotificationGateway emailNotificationGateway) {
        this.emailNotificationGateway = emailNotificationGateway;
    }

    public void execute(String toEmail, UUID orderId, OrderStatus status) {
        emailNotificationGateway.sendEmail(toEmail, orderId, status);
    }
}
