package com.fiap.techChallenge._webApi.controller.payment;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fiap.techChallenge.core.application.dto.payment.PaymentRequestDTO;
import com.fiap.techChallenge.core.application.dto.payment.PaymentResponseDTO;
import com.fiap.techChallenge.core.controller.payment.PaymentController;
import com.fiap.techChallenge.core.domain.enums.PaymentStatus;
import com.fiap.techChallenge.core.interfaces.CompositeDataSource;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/payment")
@Tag(name = "Pagamento de Pedidos", description = "API relacionada ao pagamento")
public class PaymentWebController {

    private final PaymentController paymentController;

    public PaymentWebController(
            CompositeDataSource compositeDataSource) {
        this.paymentController = PaymentController.build(compositeDataSource);
    }

    @PostMapping
    @Operation(summary = "criar pagamento por qrCode.",
            description = "Criação do qrCode para fazer o pagamento do pedido")
    public ResponseEntity<PaymentResponseDTO> processPayment(@RequestBody PaymentRequestDTO request) {
        PaymentResponseDTO response = paymentController.processPayment(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/find-payment-id/{orderId}")
    @Operation(summary = "Encontra o payment ID do pedido.",
            description = "retorna o payment ID do pedido.")
    public ResponseEntity<String> findPaymentByOrderId(@PathVariable UUID orderId) {
        String response = paymentController.findPaymentByOrderId(orderId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("{orderId}")
    @Operation(summary = "Consultar status de pagamento do pedido.",
            description = "retorna o  status de pagamento do pedido.")
    public ResponseEntity<PaymentStatus> processPayment(@PathVariable UUID orderId) {
        PaymentStatus response = paymentController.getStatus(orderId);
        return ResponseEntity.ok(response);
    }
}
