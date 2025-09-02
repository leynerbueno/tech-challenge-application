package com.fiap.techChallenge.core.notification;

import com.fiap.techChallenge.core.application.useCases.notification.NotificationUseCase;
import com.fiap.techChallenge.core.gateways.notification.EmailNotificationGateway;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;

import com.fiap.techChallenge.core.domain.enums.OrderStatus;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private EmailNotificationGateway emailNotificationGateway;

    @InjectMocks
    private NotificationUseCase notificationService;

    private final String toEmail = "cliente@exemplo.com";
    private final UUID orderId = UUID.randomUUID();
    private final OrderStatus status = OrderStatus.RECEBIDO;

    @Test
    void shouldCallGatewayWhenNotifyingStatus() {
        // Act
        notificationService.execute(toEmail, orderId, status);

        // Assert
        verify(emailNotificationGateway).sendEmail(toEmail, orderId, status);
    }

    @Test
    void shouldImplementNotificationServiceInterface() {
        // Assert
        assertTrue(notificationService instanceof NotificationUseCase,
                "NotificationUseCaseImpl deve implementar NotificationService");
    }
}