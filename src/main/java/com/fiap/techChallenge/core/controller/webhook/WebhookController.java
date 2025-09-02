package com.fiap.techChallenge.core.controller.webhook;

import org.springframework.mail.javamail.JavaMailSender;

import com.fiap.techChallenge.core.application.services.order.OrderStatusUpdaterService;
import com.fiap.techChallenge.core.application.useCases.payment.UpdateOrderStatusAfterPaymentUseCase;
import com.fiap.techChallenge.core.gateways.notification.EmailNotificationGateway;
import com.fiap.techChallenge.core.gateways.notification.EmailNotificationGatewayImpl;
import com.fiap.techChallenge.core.gateways.order.OrderGateway;
import com.fiap.techChallenge.core.gateways.order.OrderGatewayImpl;
import com.fiap.techChallenge.core.gateways.payment.PaymentGateway;
import com.fiap.techChallenge.core.gateways.payment.PaymentGatewayImpl;
import com.fiap.techChallenge.core.gateways.user.AttendantGateway;
import com.fiap.techChallenge.core.gateways.user.AttendantGatewayImpl;
import com.fiap.techChallenge.core.interfaces.CompositeDataSource;

public class WebhookController {

    private final OrderGateway orderGateway;
    private final PaymentGateway paymentGateway;
    private final OrderStatusUpdaterService orderStatusUpdaterService;

    private WebhookController(CompositeDataSource compositeDataSource, JavaMailSender javaMailSender, String mailFrom) {
        AttendantGateway attendantGateway = new AttendantGatewayImpl(compositeDataSource);
        EmailNotificationGateway emailGateway = new EmailNotificationGatewayImpl(javaMailSender, mailFrom);
        this.orderGateway = new OrderGatewayImpl(compositeDataSource);
        this.paymentGateway = new PaymentGatewayImpl(compositeDataSource);
        this.orderStatusUpdaterService = new OrderStatusUpdaterService(this.orderGateway, attendantGateway, emailGateway);
    }

    public static WebhookController build(CompositeDataSource compositeDataSource, JavaMailSender javaMailSender, String mailFrom) {
        return new WebhookController(compositeDataSource, javaMailSender, mailFrom);
    }

    public void updateStatus(String paymentId) {
        UpdateOrderStatusAfterPaymentUseCase processPaymentUseCase
                = new UpdateOrderStatusAfterPaymentUseCase(this.paymentGateway, this.orderGateway, this.orderStatusUpdaterService);
        processPaymentUseCase.execute(paymentId);
    }
}
