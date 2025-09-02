package com.fiap.techChallenge.core.application.useCases.order;

import com.fiap.techChallenge.core.domain.exceptions.EntityNotFoundException;
import com.fiap.techChallenge.core.application.dto.order.OrderWithItemsAndStatusDTO;
import com.fiap.techChallenge.core.gateways.order.OrderGateway;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes para FindOrderByIdUseCase")
class FindOrderByIdUseCaseTest {

    @Mock
    private OrderGateway orderGateway;

    @InjectMocks
    private FindOrderByIdUseCase findOrderByIdUseCase;

    @Nested
    @DisplayName("Cenários de Sucesso")
    class SuccessTests {

        @Test
        @DisplayName("Deve retornar o DTO do pedido quando encontrado pelo gateway")
        void shouldReturnDtoWhenOrderIsFound() {
            UUID orderId = UUID.randomUUID();
            OrderWithItemsAndStatusDTO mockDto = mock(OrderWithItemsAndStatusDTO.class);
            when(orderGateway.findWithDetailsById(orderId)).thenReturn(Optional.of(mockDto));

            OrderWithItemsAndStatusDTO result = findOrderByIdUseCase.execute(orderId);

            assertNotNull(result);
            assertEquals(mockDto, result);
            verify(orderGateway, times(1)).findWithDetailsById(orderId);
        }
    }

    @Nested
    @DisplayName("Cenários de Falha")
    class FailureTests {

        @Test
        @DisplayName("Deve lançar EntityNotFoundException quando o gateway retorna um Optional vazio")
        void shouldThrowExceptionWhenOrderIsNotFound() {
            UUID nonExistentId = UUID.randomUUID();
            when(orderGateway.findWithDetailsById(nonExistentId)).thenReturn(Optional.empty());

            assertThrows(EntityNotFoundException.class, () -> {
                findOrderByIdUseCase.execute(nonExistentId);
            });

            verify(orderGateway, times(1)).findWithDetailsById(nonExistentId);
        }
    }
}
