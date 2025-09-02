package com.fiap.techChallenge.core.application.useCases.order;

import com.fiap.techChallenge.core.application.dto.order.OrderWithStatusDTO;
import com.fiap.techChallenge.core.gateways.order.OrderGateway;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes para ListOrdersByPeriodUseCase")
class ListOrdersByPeriodUseCaseTest {

    @Mock
    private OrderGateway orderGateway;

    @InjectMocks
    private ListOrdersByPeriodUseCase listOrdersByPeriodUseCase;

    @Nested
    @DisplayName("Cenários de Sucesso")
    class SuccessTests {

        @Test
        @DisplayName("Deve retornar uma lista de pedidos encontrados no período")
        void shouldReturnListOfOrdersWhenFound() {
            LocalDateTime initialDt = LocalDateTime.now().minusDays(1);
            LocalDateTime finalDt = LocalDateTime.now();
            List<OrderWithStatusDTO> expectedList = List.of(mock(OrderWithStatusDTO.class));
            when(orderGateway.listByPeriod(initialDt, finalDt)).thenReturn(expectedList);

            List<OrderWithStatusDTO> result = listOrdersByPeriodUseCase.execute(initialDt, finalDt);

            assertNotNull(result);
            assertEquals(expectedList, result);
            verify(orderGateway, times(1)).listByPeriod(initialDt, finalDt);
        }

        @Test
        @DisplayName("Deve retornar uma lista vazia quando nenhum pedido for encontrado no período")
        void shouldReturnEmptyListWhenNoOrdersAreFound() {
            LocalDateTime initialDt = LocalDateTime.now().minusDays(1);
            LocalDateTime finalDt = LocalDateTime.now();

            when(orderGateway.listByPeriod(initialDt, finalDt)).thenReturn(Collections.emptyList());

            List<OrderWithStatusDTO> result = listOrdersByPeriodUseCase.execute(initialDt, finalDt);

            assertNotNull(result);
            assertTrue(result.isEmpty());
            verify(orderGateway, times(1)).listByPeriod(initialDt, finalDt);
        }
    }
}
