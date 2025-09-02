package com.fiap.techChallenge.core.application.useCases.payment;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fiap.techChallenge.core.application.dto.payment.PaymentRequestDTO;
import com.fiap.techChallenge.core.application.dto.payment.PaymentResponseDTO;
import com.fiap.techChallenge.core.application.useCases.order.UpdateOrderIdPaymentUseCase;
import com.fiap.techChallenge.core.gateways.payment.PaymentGateway;

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

        // Act
        PaymentResponseDTO result = processPaymentUseCase.execute(paymentRequestDTO);

        // Assert
        assertNotNull(result);
        assertEquals("PENDENTE", result.getStatus());
        assertEquals(orderId, result.getOrderId());
        assertEquals("https://url.qrcode/imagem.png", result.getQrCodeImage());

        verify(paymentGateway, times(1)).process(paymentRequestDTO);
    }
}
