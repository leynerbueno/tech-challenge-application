package com.fiap.techChallenge.core.application.useCases.payment;

import com.fiap.techChallenge.core.application.dto.payment.PaymentRequestDTO;
import com.fiap.techChallenge.core.application.dto.payment.PaymentResponseDTO;
import com.fiap.techChallenge.core.application.useCases.order.UpdateOrderIdPaymentUseCase;
import com.fiap.techChallenge.core.gateways.payment.PaymentGateway;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.UUID;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes para ProcessPaymentUseCase")
class ProcessPaymentUseCaseTest {

    @Mock
    private PaymentGateway paymentGateway;

    @Mock
    private UpdateOrderIdPaymentUseCase updateOrderIdPaymentUseCase;

    @InjectMocks
    private ProcessPaymentUseCase processPaymentUseCase;

    private PaymentRequestDTO paymentRequestDTO;
    private PaymentResponseDTO paymentResponseDTO;
    private UUID orderId;

    @BeforeEach
    void setUp() {
        orderId = UUID.randomUUID();
        paymentRequestDTO = new PaymentRequestDTO(orderId);
        paymentResponseDTO = new PaymentResponseDTO("PENDENTE", orderId, "https://url.qrcode/imagem.png");
    }

    @Test
    @DisplayName("Deve processar o pagamento e atualizar ID da ordem com sucesso")
    void shouldProcessPaymentAndUpdateOrderIdSuccessfully() {
        // Arrange
        when(paymentGateway.process(paymentRequestDTO)).thenReturn(paymentResponseDTO);
        when(paymentGateway.findPaymentByOrderId(orderId)).thenReturn("idPagamento123");

        // Act
        PaymentResponseDTO result = processPaymentUseCase.execute(paymentRequestDTO);

        // Assert
        assertNotNull(result);
        assertEquals("PENDENTE", result.getStatus());
        assertEquals(orderId, result.getOrderId());
        assertEquals("https://url.qrcode/imagem.png", result.getQrCodeImage());

        verify(paymentGateway, times(1)).process(paymentRequestDTO);
        verify(paymentGateway, times(1)).findPaymentByOrderId(orderId);
        verify(updateOrderIdPaymentUseCase, times(1)).execute(orderId, "idPagamento123");
    }
}
