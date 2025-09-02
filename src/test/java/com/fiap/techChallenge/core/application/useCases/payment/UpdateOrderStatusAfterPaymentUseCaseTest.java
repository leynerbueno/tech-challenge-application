package com.fiap.techChallenge.core.application.useCases.payment;

import com.fiap.techChallenge.core.application.services.order.OrderStatusUpdaterService;
import com.fiap.techChallenge.core.gateways.payment.PaymentGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes para UpdateOrderStatusAfterPaymentUseCase")
class UpdateOrderStatusAfterPaymentUseCaseTest {

    @Mock
    private PaymentGateway paymentGateway;

    @Mock
    private OrderStatusUpdaterService orderStatusUpdaterService;

    @InjectMocks
    private UpdateOrderStatusAfterPaymentUseCase updateOrderStatusAfterPaymentUseCase;

    private String paymentId;
    private UUID orderId;

    @BeforeEach
    void setUp() {
        paymentId = "pagamento123";
        orderId = UUID.randomUUID();
    }

    @Test
    @DisplayName("Deve buscar o ID do pedido e mover o status para pago")
    void shouldFindOrderIdAndMoveStatusToPaid() {
        when(paymentGateway.findApprovedOrderByPaymentId(paymentId)).thenReturn(orderId);

        updateOrderStatusAfterPaymentUseCase.execute(paymentId);

        verify(paymentGateway, times(1)).findApprovedOrderByPaymentId(paymentId);
        verify(orderStatusUpdaterService, times(1)).moveStatusToPaid(orderId);
    }

    @Test
    @DisplayName("Deve lançar exceção quando o ID do pedido não for encontrado")
    void shouldThrowExceptionWhenOrderIdIsNull() {
        when(paymentGateway.findApprovedOrderByPaymentId(paymentId)).thenReturn(null);

        assertThrows(IllegalArgumentException.class, () -> {
            updateOrderStatusAfterPaymentUseCase.execute(paymentId);
        });

        verifyNoInteractions(orderStatusUpdaterService);
    }
}
