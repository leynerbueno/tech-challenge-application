package com.fiap.techChallenge.core.application.useCases.order;

import com.fiap.techChallenge.core.application.dto.order.OrderWithStatusAndWaitMinutesDTO;
import com.fiap.techChallenge.core.domain.enums.OrderStatus;
import com.fiap.techChallenge.core.gateways.order.OrderGateway;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;



import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes para ListTodayOrdersUseCase")
class ListTodayOrdersUseCaseTest {

    @Mock
    private OrderGateway orderGateway;

    @InjectMocks
    private ListTodayOrdersUseCase listTodayOrdersUseCase;

    @Nested
    @DisplayName("Cenários de Sucesso")
    class SuccessTests {

        @Test
        @DisplayName("Deve chamar o gateway com a lista de status correta e retornar os pedidos")
        void shouldCallGatewayWithCorrectStatusListAndReturnOrders() {
            List<OrderWithStatusAndWaitMinutesDTO> expectedList = List.of(mock(OrderWithStatusAndWaitMinutesDTO.class));
            when(orderGateway.listTodayOrders(anyList(), anyInt())).thenReturn(expectedList);

            List<OrderWithStatusAndWaitMinutesDTO> result = listTodayOrdersUseCase.execute();

            assertNotNull(result);
            assertEquals(expectedList, result);

            ArgumentCaptor<List<String>> statusCaptor = ArgumentCaptor.forClass(List.class);
            ArgumentCaptor<Integer> limitCaptor = ArgumentCaptor.forClass(Integer.class);
            
            verify(orderGateway, times(1)).listTodayOrders(statusCaptor.capture(), limitCaptor.capture());

            List<String> capturedStatusList = statusCaptor.getValue();
            assertTrue(capturedStatusList.contains(OrderStatus.RECEBIDO.name()));
            assertTrue(capturedStatusList.contains(OrderStatus.EM_PREPARACAO.name()));
            assertTrue(capturedStatusList.contains(OrderStatus.PRONTO.name()));
            assertEquals(3, capturedStatusList.size());
            assertEquals(5, limitCaptor.getValue());
        }

        @Test
        @DisplayName("Deve retornar uma lista vazia quando o gateway não encontra pedidos")
        void shouldReturnEmptyListWhenNoOrdersAreFound() {
            when(orderGateway.listTodayOrders(anyList(), anyInt())).thenReturn(Collections.emptyList());

            List<OrderWithStatusAndWaitMinutesDTO> result = listTodayOrdersUseCase.execute();

            assertNotNull(result);
            assertTrue(result.isEmpty());
        }
    }
}