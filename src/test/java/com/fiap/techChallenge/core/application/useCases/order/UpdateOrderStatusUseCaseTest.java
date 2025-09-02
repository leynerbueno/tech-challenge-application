package com.fiap.techChallenge.core.application.useCases.order;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fiap.techChallenge.core.application.dto.order.UpdateOrderStatusInputDTO;
import com.fiap.techChallenge.core.application.services.order.OrderStatusUpdaterService;
import com.fiap.techChallenge.core.domain.entities.order.Order;
import com.fiap.techChallenge.core.domain.enums.OrderStatus;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes para UpdateOrderStatusUseCase")
class UpdateOrderStatusUseCaseTest {

    @Mock
    private OrderStatusUpdaterService orderStatusUpdaterService;

    @InjectMocks
    private UpdateOrderStatusUseCase updateOrderStatusUseCase;

    private UUID orderId;
    private UUID attendantId;

    @BeforeEach
    void setUp() {
        orderId = UUID.randomUUID();
        attendantId = UUID.randomUUID();
    }

    @Test
    @DisplayName("Deve chamar moveStatusToReceived quando o status for RECEBIDO")
    void shouldCallMoveStatusToReceivedWhenStatusIsRecebido() {
        UpdateOrderStatusInputDTO dto = new UpdateOrderStatusInputDTO(orderId, OrderStatus.RECEBIDO, attendantId);
        when(orderStatusUpdaterService.moveStatusToReceived(orderId, attendantId)).thenReturn(mock(Order.class));

        updateOrderStatusUseCase.execute(dto);

        verify(orderStatusUpdaterService, times(1)).moveStatusToReceived(orderId, attendantId);
        verifyNoMoreInteractions(orderStatusUpdaterService);
    }



    

    @Test
    @DisplayName("Deve chamar moveStatusToInPreparation quando o status for EM_PREPARACAO")
    void shouldCallMoveStatusToInPreparation() {
        UpdateOrderStatusInputDTO dto = new UpdateOrderStatusInputDTO(orderId, OrderStatus.EM_PREPARACAO, attendantId);

        updateOrderStatusUseCase.execute(dto);

        verify(orderStatusUpdaterService).moveStatusToInPreparation(orderId, attendantId);
        verifyNoMoreInteractions(orderStatusUpdaterService);
    }

    @Test
    @DisplayName("Deve chamar moveStatusToReady quando o status for PRONTO")
    void shouldCallMoveStatusToReady() {
        UpdateOrderStatusInputDTO dto = new UpdateOrderStatusInputDTO(orderId, OrderStatus.PRONTO, attendantId);

        updateOrderStatusUseCase.execute(dto);

        verify(orderStatusUpdaterService).moveStatusToReady(orderId, attendantId);
        verifyNoMoreInteractions(orderStatusUpdaterService);
    }

    @Test
    @DisplayName("Deve chamar moveStatusToFinished quando o status for FINALIZADO")
    void shouldCallMoveStatusToFinished() {
        UpdateOrderStatusInputDTO dto = new UpdateOrderStatusInputDTO(orderId, OrderStatus.FINALIZADO, attendantId);

        updateOrderStatusUseCase.execute(dto);

        verify(orderStatusUpdaterService).moveStatusToFinished(orderId, attendantId);
        verifyNoMoreInteractions(orderStatusUpdaterService);
    }

    @Test
    @DisplayName("Deve chamar moveStatusToCanceled quando o status for CANCELADO")
    void shouldCallMoveStatusToCanceled() {
        UpdateOrderStatusInputDTO dto = new UpdateOrderStatusInputDTO(orderId, OrderStatus.CANCELADO, attendantId);

        updateOrderStatusUseCase.execute(dto);

        verify(orderStatusUpdaterService).moveStatusToCanceled(orderId, attendantId);
        verifyNoMoreInteractions(orderStatusUpdaterService);
    }

    @Test
    @DisplayName("Deve lançar exceção para um status não mapeado no switch")
    void shouldThrowExceptionForUnmappedStatus() {
        // PAGAMENTO_PENDENTE é um status válido, mas não é um destino válido para este caso
        UpdateOrderStatusInputDTO dto = new UpdateOrderStatusInputDTO(orderId, OrderStatus.PAGAMENTO_PENDENTE, attendantId);

        assertThrows(IllegalArgumentException.class, () -> {
            updateOrderStatusUseCase.execute(dto);
        });

        verifyNoInteractions(orderStatusUpdaterService);
    }
}
