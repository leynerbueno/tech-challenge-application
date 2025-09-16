package com.fiap.techChallenge.core.domain.order;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;



import com.fiap.techChallenge.core.domain.exceptions.EntityNotFoundException;
import com.fiap.techChallenge.core.domain.exceptions.order.InvalidOrderStatusException;
import com.fiap.techChallenge.core.domain.entities.order.Order;
import com.fiap.techChallenge.core.domain.entities.order.OrderItem;
import com.fiap.techChallenge.core.domain.entities.order.OrderStatusHistory;
import com.fiap.techChallenge.core.domain.entities.product.Product;
import com.fiap.techChallenge.core.domain.entities.user.attendant.Attendant;
import com.fiap.techChallenge.core.domain.entities.user.customer.Customer;
import com.fiap.techChallenge.core.domain.enums.Category;
import com.fiap.techChallenge.core.domain.enums.OrderStatus;
import com.fiap.techChallenge.core.domain.enums.ProductStatus;

@DisplayName("Testes para a Entidade Order")
class OrderTest {

    private Customer customer;
    private Product product1;
    private Product product2;

    @BeforeEach
    void setUp() {
        customer = mock(Customer.class);
        when(customer.getId()).thenReturn(UUID.randomUUID());

        product1 = Product.build(
                UUID.randomUUID(),
                "X-Burger Clássico",
                "Pão, hambúrguer de 150g e queijo",
                new BigDecimal("25.50"),
                Category.LANCHE,
                ProductStatus.DISPONIVEL,
                "xburger.png"
        );

        product2 = Product.build(
                UUID.randomUUID(),
                "Refrigerante de Cola",
                "Lata 350ml",
                new BigDecimal("8.00"),
                Category.BEBIDA,
                ProductStatus.DISPONIVEL,
                "refri.png"
        );
    }

    private Order createOrderWithStatus(OrderStatus initialStatus, int initialQuantity) {
        OrderItem item = OrderItem.build(product1, initialQuantity);
        OrderStatusHistory history = OrderStatusHistory.build(null, initialStatus, LocalDateTime.now().minusMinutes(5));
        return Order.build(UUID.randomUUID(), new ArrayList<>(List.of(item)), new ArrayList<>(List.of(history)), customer, LocalDateTime.now(), null);
    }

    @Nested
    @DisplayName("Testes de Criação")
    class CreationTests {

        @Nested
        @DisplayName("Cenários de Sucesso")
        class Success {

            @Test
            @DisplayName("Deve criar um pedido com sucesso com dados válidos")
            void shouldBuildOrderSuccessfully() {
                OrderItem item = OrderItem.build(product1, 1);
                OrderStatusHistory history = OrderStatusHistory.build(null, OrderStatus.PAGAMENTO_PENDENTE, LocalDateTime.now());
                Order order = Order.build(UUID.randomUUID(), List.of(item), List.of(history), customer, LocalDateTime.now(), null);
                assertNotNull(order);
            }
        }

        @Nested
        @DisplayName("Cenários de Falha")
        class Failure {

            @Test
            @DisplayName("Deve lançar exceção se a lista de itens for nula ou vazia")
            void shouldThrowExceptionWhenItemsAreEmpty() {
                assertThrows(IllegalArgumentException.class, ()
                        -> Order.build(UUID.randomUUID(), Collections.emptyList(), List.of(mock(OrderStatusHistory.class)), customer, LocalDateTime.now(), null)
                );
            }

            @Test
            @DisplayName("Deve lançar exceção se o histórico de status for nulo ou vazio")
            void shouldThrowExceptionWhenStatusHistoryIsEmpty() {
                assertThrows(IllegalArgumentException.class, ()
                        -> Order.build(UUID.randomUUID(), List.of(mock(OrderItem.class)), Collections.emptyList(), customer, LocalDateTime.now(), null)
                );
            }
        }
    }

    @Nested
    @DisplayName("Testes de Propriedades Calculadas")
    class ComputedPropertiesTests {

        @Nested
        @DisplayName("Cenários de Sucesso")
        class Success {

            @Test
            @DisplayName("Deve calcular o preço total corretamente")
            void shouldCalculateTotalPriceCorrectly() {
                Order order = createOrderWithStatus(OrderStatus.PAGAMENTO_PENDENTE, 2); // 2 * 25.50 = 51.00
                order.addItem(product2, 1); // 1 * 8.00 = 8.00. Total = 59.00
                assertEquals(0, new BigDecimal("59.00").compareTo(order.getPrice()));
            }

            @Test
            @DisplayName("Deve retornar o status atual mais recente")
            void shouldReturnLatestStatus() {
                Order order = createOrderWithStatus(OrderStatus.PAGO, 1);
                order.moveStatusToReceived(mock(Attendant.class));
                assertEquals(OrderStatus.RECEBIDO, order.getCurrentStatus());
            }
        }
    }

    @Nested
    @DisplayName("Testes de Modificação de Itens")
    class ItemModificationTests {

        @Nested
        @DisplayName("Cenários de Sucesso")
        class Success {

            @Test
            @DisplayName("Deve adicionar um novo item ao pedido")
            void shouldAddNewItemToOrder() {
                Order order = createOrderWithStatus(OrderStatus.PAGAMENTO_PENDENTE, 1);
                order.addItem(product2, 1);
                assertEquals(2, order.getItems().size());
            }

            @Test
            @DisplayName("Deve aumentar a quantidade de um item existente")
            void shouldIncreaseQuantityOfExistingItem() {
                Order order = createOrderWithStatus(OrderStatus.PAGAMENTO_PENDENTE, 2);
                order.addItem(product1, 3);
                assertEquals(5, order.getItems().get(0).getQuantity());
            }

            @Test
            @DisplayName("Deve diminuir a quantidade de um item")
            void shouldDecreaseItemQuantity() {
                Order order = createOrderWithStatus(OrderStatus.PAGAMENTO_PENDENTE, 5);
                order.removeItem(product1.getId(), 2);
                assertEquals(3, order.getItems().get(0).getQuantity());
            }
        }

        @Nested
        @DisplayName("Cenários de Falha")
        class Failure {

            @Test
            @DisplayName("Deve lançar exceção ao adicionar item em pedido finalizado")
            void shouldThrowWhenAddingToFinishedOrder() {
                Order order = createOrderWithStatus(OrderStatus.FINALIZADO, 1);
                assertThrows(InvalidOrderStatusException.class, () -> order.addItem(product2, 1));
            }

            @Test
            @DisplayName("Deve lançar exceção ao remover item de um pedido que não está com pagamento pendente")
            void shouldThrowWhenRemovingFromNonPendingOrder() {
                Order order = createOrderWithStatus(OrderStatus.PAGO, 2);
                assertThrows(IllegalStateException.class, () -> order.removeItem(product1.getId(), 1));
            }

            @Test
            @DisplayName("Deve lançar exceção ao remover item inexistente")
            void shouldThrowWhenRemovingNonExistentItem() {
                Order order = createOrderWithStatus(OrderStatus.PAGAMENTO_PENDENTE, 2);
                assertThrows(EntityNotFoundException.class, () -> order.removeItem(UUID.randomUUID(), 1));
            }

            @Test
            @DisplayName("Deve lançar exceção ao deixar o pedido com menos de um item")
            void shouldThrowWhenLeavingOrderWithLessThanOneItem() {
                Order order = createOrderWithStatus(OrderStatus.PAGAMENTO_PENDENTE, 1);
                assertThrows(IllegalStateException.class, () -> order.removeItem(product1.getId(), 1));
            }
        }
    }

    @Nested
    @DisplayName("Testes de Transição de Status")
    class StatusTransitionTests {

        @Nested
        @DisplayName("Cenários de Sucesso")
        class Success {

            @Test
            @DisplayName("Deve transitar por todos os status válidos em sequência")
            void shouldTransitionThroughAllValidStatuses() {
                Order order = createOrderWithStatus(OrderStatus.PAGAMENTO_PENDENTE, 1);

                assertDoesNotThrow(() -> order.moveStatusToPaid());
                assertEquals(OrderStatus.PAGO, order.getCurrentStatus());

                assertDoesNotThrow(() -> order.moveStatusToReceived(mock(Attendant.class)));
                assertEquals(OrderStatus.RECEBIDO, order.getCurrentStatus());

                assertDoesNotThrow(() -> order.moveStatusToInPreparation(mock(Attendant.class)));
                assertEquals(OrderStatus.EM_PREPARACAO, order.getCurrentStatus());

                assertDoesNotThrow(() -> order.moveStatusToReady(mock(Attendant.class)));
                assertEquals(OrderStatus.PRONTO, order.getCurrentStatus());

                assertDoesNotThrow(() -> order.moveStatusToFinished(mock(Attendant.class)));
                assertEquals(OrderStatus.FINALIZADO, order.getCurrentStatus());
            }

            @Test
            @DisplayName("Deve permitir o cancelamento de um pedido em preparação")
            void shouldAllowCanceling() {
                Order order = createOrderWithStatus(OrderStatus.EM_PREPARACAO, 1);
                order.moveStatusToCanceled(mock(Attendant.class));
                assertEquals(OrderStatus.CANCELADO, order.getCurrentStatus());
            }
        }

        @Nested
        @DisplayName("Cenários de Falha")
        class Failure {

            @Test
            @DisplayName("Deve lançar exceção em uma transição de status inválida (PAGO para EM_PREPARACAO)")
            void shouldThrowOnInvalidTransition() {
                Order order = createOrderWithStatus(OrderStatus.PAGO, 1);
                assertThrows(InvalidOrderStatusException.class, () -> order.moveStatusToInPreparation(mock(Attendant.class)));
            }

            @Test
            @DisplayName("Deve lançar exceção ao tentar alterar status de um pedido finalizado")
            void shouldThrowWhenChangingStatusOfFinishedOrder() {
                Order order = createOrderWithStatus(OrderStatus.FINALIZADO, 1);
                assertThrows(InvalidOrderStatusException.class, () -> order.moveStatusToReady(mock(Attendant.class)));
            }
        }
    }
}
