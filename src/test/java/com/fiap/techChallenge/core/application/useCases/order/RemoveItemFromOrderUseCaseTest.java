package com.fiap.techChallenge.core.application.useCases.order;

import com.fiap.techChallenge.core.domain.exceptions.EntityNotFoundException;
import com.fiap.techChallenge.core.domain.entities.order.Order;
import com.fiap.techChallenge.core.domain.entities.order.OrderItem;
import com.fiap.techChallenge.core.domain.entities.order.OrderStatusHistory;
import com.fiap.techChallenge.core.domain.entities.product.Product;
import com.fiap.techChallenge.core.domain.entities.user.customer.Customer;
import com.fiap.techChallenge.core.domain.enums.Category;
import com.fiap.techChallenge.core.domain.enums.OrderStatus;
import com.fiap.techChallenge.core.domain.enums.ProductStatus;
import com.fiap.techChallenge.core.gateways.order.OrderGateway;
import com.fiap.techChallenge.core.gateways.product.ProductGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes para RemoveItemFromOrderUseCase")
class RemoveItemFromOrderUseCaseTest {

    @Mock
    private OrderGateway orderGateway;

    @Mock
    private ProductGateway productGateway;

    @Mock
    private Customer customer;

    @InjectMocks
    private RemoveItemFromOrderUseCase removeItemFromOrderUseCase;

    @BeforeEach
    void setUp() {
        lenient().when(customer.getId()).thenReturn(UUID.randomUUID());
    }

    @Test
    @DisplayName("Deve diminuir a quantidade de um item do pedido com sucesso")
    void shouldDecreaseItemQuantityFromOrderSuccessfully() {
        UUID orderId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        int initialQuantity = 3;
        int quantityToRemove = 2;

        Product product = Product.build(
                productId, "Product Name", "Description", new BigDecimal("10.0"),
                Category.LANCHE, ProductStatus.DISPONIVEL, "image.png"
        );
        OrderItem item = OrderItem.build(product, initialQuantity);
        OrderStatusHistory status = OrderStatusHistory.build(null, OrderStatus.PAGAMENTO_PENDENTE, LocalDateTime.now());
        Order order = Order.build(orderId, new ArrayList<>(List.of(item)), new ArrayList<>(List.of(status)), customer, LocalDateTime.now(), null);

        when(orderGateway.findById(orderId)).thenReturn(order);
        when(productGateway.findById(productId)).thenReturn(product);
        when(orderGateway.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Order result = removeItemFromOrderUseCase.execute(orderId, productId, quantityToRemove);

        assertNotNull(result);
        assertEquals(1, result.getItems().size());
        assertEquals(1, result.getItems().get(0).getQuantity());
        verify(orderGateway, times(1)).findById(orderId);
        verify(productGateway, times(1)).findById(productId);
        verify(orderGateway, times(1)).save(result);
    }

    @Test
    @DisplayName("Deve lançar EntityNotFoundException quando o pedido não for encontrado")
    void shouldThrowEntityNotFoundExceptionWhenOrderIsNotFound() {
        UUID orderId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        int quantity = 1;

        when(orderGateway.findById(orderId)).thenReturn(null);

        assertThrows(EntityNotFoundException.class, () -> {
            removeItemFromOrderUseCase.execute(orderId, productId, quantity);
        });

        verify(productGateway, never()).findById(any());
        verify(orderGateway, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar EntityNotFoundException quando o produto não for encontrado")
    void shouldThrowEntityNotFoundExceptionWhenProductIsNotFound() {
        UUID orderId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        int quantity = 1;

        Product mockedProduct = mock(Product.class);
        when(mockedProduct.getId()).thenReturn(UUID.randomUUID());
        when(mockedProduct.getName()).thenReturn("Mocked Product");
        when(mockedProduct.getPrice()).thenReturn(BigDecimal.TEN);
        when(mockedProduct.getCategory()).thenReturn(Category.ACOMPANHAMENTO);

        OrderItem item = OrderItem.build(mockedProduct, quantity);
        OrderStatusHistory status = OrderStatusHistory.build(null, OrderStatus.PAGAMENTO_PENDENTE, LocalDateTime.now());
        Order order = Order.build(orderId, new ArrayList<>(List.of(item)), new ArrayList<>(List.of(status)), customer, LocalDateTime.now(), null);

        when(orderGateway.findById(orderId)).thenReturn(order);
        when(productGateway.findById(productId)).thenReturn(null);

        assertThrows(EntityNotFoundException.class, () -> {
            removeItemFromOrderUseCase.execute(orderId, productId, quantity);
        });

        verify(orderGateway, times(1)).findById(orderId);
        verify(productGateway, times(1)).findById(productId);
        verify(orderGateway, never()).save(any());
    }

    @Test
    @DisplayName("Deve propagar exceção ao tentar remover item de pedido com status inválido")
    void shouldPropagateExceptionWhenRemovingItemFromOrderWithInvalidStatus() {
        UUID orderId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        int quantity = 1;

        Product product = Product.build(
                productId, "Product Name", "Description", new BigDecimal("10.0"),
                Category.LANCHE, ProductStatus.DISPONIVEL, "image.png"
        );
        OrderItem item = OrderItem.build(product, quantity);
        OrderStatusHistory status = OrderStatusHistory.build(null, OrderStatus.PAGO, LocalDateTime.now());
        Order order = Order.build(orderId, new ArrayList<>(List.of(item)), new ArrayList<>(List.of(status)), customer, LocalDateTime.now(), "payment123");

        when(orderGateway.findById(orderId)).thenReturn(order);
        when(productGateway.findById(productId)).thenReturn(product);

        assertThrows(IllegalStateException.class, () -> {
            removeItemFromOrderUseCase.execute(orderId, productId, quantity);
        });

        verify(orderGateway, times(1)).findById(orderId);
        verify(productGateway, times(1)).findById(productId);
        verify(orderGateway, never()).save(any());
    }
}
