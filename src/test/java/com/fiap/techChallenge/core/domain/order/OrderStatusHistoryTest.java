package com.fiap.techChallenge.core.domain.order;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mock;

import com.fiap.techChallenge.core.domain.entities.order.OrderStatusHistory;
import com.fiap.techChallenge.core.domain.entities.user.attendant.Attendant;
import com.fiap.techChallenge.core.domain.enums.OrderStatus;

@DisplayName("Testes para a Entidade OrderStatusHistory")
class OrderStatusHistoryTest {

    @Nested
    @DisplayName("Testes de Criação (build)")
    class CreationTests {

        @Nested
        @DisplayName("Cenários de Sucesso")
        class Success {

            @Test
            @DisplayName("Deve criar um histórico com atendente")
            void shouldBuildWithAttendant() {
                Attendant attendant = mock(Attendant.class);
                LocalDateTime now = LocalDateTime.now();

                OrderStatusHistory history = OrderStatusHistory.build(attendant, OrderStatus.EM_PREPARACAO, now);

                assertNotNull(history);
                assertEquals(attendant, history.getAttendant());
                assertEquals(OrderStatus.EM_PREPARACAO, history.getStatus());
                assertEquals(now, history.getDate());
            }

            @Test
            @DisplayName("Deve criar um histórico sem atendente (nulo)")
            void shouldBuildWithoutAttendant() {
                LocalDateTime now = LocalDateTime.now();

                OrderStatusHistory history = OrderStatusHistory.build(null, OrderStatus.PAGO, now);

                assertNotNull(history);
                assertNull(history.getAttendant());
                assertEquals(OrderStatus.PAGO, history.getStatus());
            }
        }

        @Nested
        @DisplayName("Cenários de Falha")
        class Failure {

            @Test
            @DisplayName("Deve lançar exceção ao criar com status nulo")
            void shouldThrowWhenStatusIsNull() {
                assertThrows(IllegalArgumentException.class, ()
                        -> OrderStatusHistory.build(null, null, LocalDateTime.now())
                );
            }

            @Test
            @DisplayName("Deve lançar exceção ao criar com data nula")
            void shouldThrowWhenDateIsNull() {
                assertThrows(IllegalArgumentException.class, ()
                        -> OrderStatusHistory.build(null, OrderStatus.RECEBIDO, null)
                );
            }
        }
    }

    @Nested
    @DisplayName("Testes de Modificação (Setters)")
    class SettersTests {

        @Nested
        @DisplayName("Cenários de Sucesso")
        class Success {

            @Test
            @DisplayName("Deve definir as propriedades corretamente")
            void shouldSetPropertiesCorrectly() {
                OrderStatusHistory history = new OrderStatusHistory();
                Attendant attendant = mock(Attendant.class);
                LocalDateTime now = LocalDateTime.now();

                assertDoesNotThrow(() -> history.setStatus(OrderStatus.PRONTO));
                assertDoesNotThrow(() -> history.setDate(now));
                assertDoesNotThrow(() -> history.setAttendantId(attendant));

                assertEquals(OrderStatus.PRONTO, history.getStatus());
                assertEquals(now, history.getDate());
                assertEquals(attendant, history.getAttendant());
            }
        }

        @Nested
        @DisplayName("Cenários de Falha")
        class Failure {

            @Test
            @DisplayName("Deve lançar exceção ao definir status nulo")
            void shouldThrowOnSetNullStatus() {
                OrderStatusHistory history = new OrderStatusHistory();
                assertThrows(IllegalArgumentException.class, () -> history.setStatus(null));
            }

            @Test
            @DisplayName("Deve lançar exceção ao definir data nula")
            void shouldThrowOnSetNullDate() {
                OrderStatusHistory history = new OrderStatusHistory();
                assertThrows(IllegalArgumentException.class, () -> history.setDate(null));
            }
        }
    }
}
