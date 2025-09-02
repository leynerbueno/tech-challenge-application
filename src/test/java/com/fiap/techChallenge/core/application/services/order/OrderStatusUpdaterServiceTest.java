package com.fiap.techChallenge.core.application.services.order;

import com.fiap.techChallenge.core.domain.exceptions.EntityNotFoundException;
import com.fiap.techChallenge.core.domain.exceptions.order.InvalidOrderStatusException;
import com.fiap.techChallenge.core.domain.entities.order.Order;
import com.fiap.techChallenge.core.domain.entities.order.OrderItem;
import com.fiap.techChallenge.core.domain.entities.order.OrderStatusHistory;
import com.fiap.techChallenge.core.domain.entities.user.attendant.Attendant;
import com.fiap.techChallenge.core.domain.entities.user.customer.Customer;
import com.fiap.techChallenge.core.domain.enums.OrderStatus;
import com.fiap.techChallenge.core.gateways.order.OrderGateway;
import com.fiap.techChallenge.core.gateways.user.AttendantGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes para OrderStatusUpdaterService")
class OrderStatusUpdaterServiceTest {

    @Mock
    private OrderGateway orderGateway;

    @Mock
    private AttendantGateway attendantGateway;

    @Mock
    private Customer customer;

    @Mock
    private OrderItem orderItem;

    @InjectMocks
    private OrderStatusUpdaterService orderStatusUpdaterService;

    private Attendant attendant;

    @BeforeEach
    void setUp() {
        attendant = Attendant.build(
                UUID.randomUUID(),
                "Attendant Name",
                "attendant@email.com",
                "12345678901"
        );

        when(customer.getId()).thenReturn(UUID.randomUUID());
    }

    private Order createTestOrder(OrderStatus initialStatus) {
        OrderStatusHistory statusHistory = OrderStatusHistory.build(null, initialStatus, LocalDateTime.now());
        return Order.build(
                UUID.randomUUID(),
                new ArrayList<>(List.of(orderItem)),
                new ArrayList<>(List.of(statusHistory)),
                customer,
                LocalDateTime.now(),
                initialStatus == OrderStatus.PAGAMENTO_PENDENTE ? null : "payment123"
        );
    }

    @Nested
    @DisplayName("Testes para moveStatusToPaid")
    class MoveStatusToPaidTests {

        @Test
        @DisplayName("Deve mover o status para PAGO com sucesso usando o ID do pedido")
        void shouldMoveStatusToPaidSuccessfullyByOrderId() {
            Order order = createTestOrder(OrderStatus.PAGAMENTO_PENDENTE);
            String paymentId = "new-payment-id";

            when(orderGateway.findById(order.getId())).thenReturn(order);
            when(orderGateway.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));

            Order result = orderStatusUpdaterService.moveStatusToPaid(order.getId());

            assertEquals(OrderStatus.PAGO, result.getCurrentStatus());
            assertEquals(paymentId, result.getPaymentId());
            verify(orderGateway).save(result);
        }

        @Test
        @DisplayName("Deve lançar exceção ao tentar mover para PAGO com status inválido")
        void shouldThrowExceptionWhenMovingToPaidWithInvalidStatus() {
            Order order = createTestOrder(OrderStatus.PAGO);

            when(orderGateway.findById(order.getId())).thenReturn(order);

            assertThrows(InvalidOrderStatusException.class, () -> {
                orderStatusUpdaterService.moveStatusToPaid(order.getId());
            });
        }
    }

    @Nested
    @DisplayName("Testes para moveStatusToReceived")
    class MoveStatusToReceivedTests {

        @Test
        @DisplayName("Deve mover o status para RECEBIDO com sucesso")
        void shouldMoveStatusToReceivedSuccessfully() {
            Order order = createTestOrder(OrderStatus.PAGO);

            when(orderGateway.findById(order.getId())).thenReturn(order);
            when(attendantGateway.findFirstById(attendant.getId())).thenReturn(attendant);
            when(orderGateway.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));

            Order result = orderStatusUpdaterService.moveStatusToReceived(order.getId(), attendant.getId());

            assertEquals(OrderStatus.RECEBIDO, result.getCurrentStatus());
            verify(orderGateway).save(result);
        }

        @Test
        @DisplayName("Deve lançar EntityNotFoundException quando o atendente não for encontrado")
        void shouldThrowEntityNotFoundExceptionWhenAttendantIsNotFound() {
            Order order = createTestOrder(OrderStatus.PAGO);
            UUID nonExistentAttendantId = UUID.randomUUID();

            when(orderGateway.findById(order.getId())).thenReturn(order);
            when(attendantGateway.findFirstById(nonExistentAttendantId)).thenReturn(null);

            assertThrows(EntityNotFoundException.class, () -> {
                orderStatusUpdaterService.moveStatusToReceived(order.getId(), nonExistentAttendantId);
            });
        }
    }

    @Nested
    @DisplayName("Testes para moveStatusToInPreparation")
    class MoveStatusToInPreparationTests {

        @Test
        @DisplayName("Deve mover o status para EM_PREPARACAO com sucesso")
        void shouldMoveStatusToInPreparationSuccessfully() {
            Order order = createTestOrder(OrderStatus.RECEBIDO);

            when(orderGateway.findById(order.getId())).thenReturn(order);
            when(attendantGateway.findFirstById(attendant.getId())).thenReturn(attendant);
            when(orderGateway.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));

            Order result = orderStatusUpdaterService.moveStatusToInPreparation(order.getId(), attendant.getId());

            assertEquals(OrderStatus.EM_PREPARACAO, result.getCurrentStatus());
            verify(orderGateway).save(result);
        }
    }

    @Nested
    @DisplayName("Testes para moveStatusToReady")
    class MoveStatusToReadyTests {

        @Test
        @DisplayName("Deve mover o status para PRONTO com sucesso")
        void shouldMoveStatusToReadySuccessfully() {
            Order order = createTestOrder(OrderStatus.EM_PREPARACAO);

            when(orderGateway.findById(order.getId())).thenReturn(order);
            when(attendantGateway.findFirstById(attendant.getId())).thenReturn(attendant);
            when(orderGateway.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));

            Order result = orderStatusUpdaterService.moveStatusToReady(order.getId(), attendant.getId());

            assertEquals(OrderStatus.PRONTO, result.getCurrentStatus());
            verify(orderGateway).save(result);
        }
    }

    @Nested
    @DisplayName("Testes para moveStatusToFinished")
    class MoveStatusToFinishedTests {

        @Test
        @DisplayName("Deve mover o status para FINALIZADO com sucesso")
        void shouldMoveStatusToFinishedSuccessfully() {
            Order order = createTestOrder(OrderStatus.PRONTO);

            when(orderGateway.findById(order.getId())).thenReturn(order);
            when(attendantGateway.findFirstById(attendant.getId())).thenReturn(attendant);
            when(orderGateway.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));

            Order result = orderStatusUpdaterService.moveStatusToFinished(order.getId(), attendant.getId());

            assertEquals(OrderStatus.FINALIZADO, result.getCurrentStatus());
            verify(orderGateway).save(result);
        }
    }

    @Nested
    @DisplayName("Testes para moveStatusToCanceled")
    class MoveStatusToCanceledTests {

        @Test
        @DisplayName("Deve mover o status para CANCELADO com sucesso")
        void shouldMoveStatusToCanceledSuccessfully() {
            Order order = createTestOrder(OrderStatus.PAGAMENTO_PENDENTE);

            when(orderGateway.findById(order.getId())).thenReturn(order);
            when(attendantGateway.findFirstById(attendant.getId())).thenReturn(attendant);
            when(orderGateway.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));

            Order result = orderStatusUpdaterService.moveStatusToCanceled(order.getId(), attendant.getId());

            assertEquals(OrderStatus.CANCELADO, result.getCurrentStatus());
            verify(orderGateway).save(result);
        }
    }
}
