package com.fiap.techChallenge._webApi.controller.webhook;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fiap.techChallenge.core.application.dto.payment.PaymentWebhookPayloadDTO;
import com.fiap.techChallenge.core.controller.webhook.WebhookController;
import com.fiap.techChallenge.core.domain.exceptions.payment.PaymentException;
import com.fiap.techChallenge.core.interfaces.CompositeDataSource;

import jakarta.validation.Valid;

@RestController
@RequestMapping("api/webhook")
public class PaymentWebhookController {

    private final WebhookController webhookController;

    public PaymentWebhookController(CompositeDataSource compositeDataSource, JavaMailSender javaMailSender, @Value("${app.mail.from}") String mailFrom) {
        this.webhookController = WebhookController.build(compositeDataSource, javaMailSender, mailFrom);
    }

    @PostMapping
    public ResponseEntity<Void> handleWebhook(@RequestBody @Valid PaymentWebhookPayloadDTO payload) {
        if ("update".equals(payload.getAction()) ) {
            if(payload.getData() != null && payload.getData().getId() != null) {
                webhookController.updateStatus(payload.getData().getId());
            } else {
                throw new PaymentException("Payment ID not found");
            }
        }
        return ResponseEntity.ok().build();
    }

}
