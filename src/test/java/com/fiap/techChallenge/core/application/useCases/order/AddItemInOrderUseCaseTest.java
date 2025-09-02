package com.fiap.techChallenge.core.application.useCases.order;

import com.fiap.techChallenge.core.domain.exceptions.EntityNotFoundException;
import com.fiap.techChallenge.core.domain.exceptions.order.InvalidOrderStatusException;
import com.fiap.techChallenge.core.domain.exceptions.product.ProductNotAvaiableException;
import com.fiap.techChallenge.core.application.services.product.ProductAvailabilityService;
import com.fiap.techChallenge.core.domain.entities.order.Order;
import com.fiap.techChallenge.core.domain.entities.order.OrderItem;
import com.fiap.techChallenge.core.domain.entities.order.OrderStatusHistory;
import com.fiap.techChallenge.core.domain.entities.product.Product;
import com.fiap.techChallenge.core.domain.entities.user.customer.Customer;
import com.fiap.techChallenge.core.domain.enums.Category;
import com.fiap.techChallenge.core.domain.enums.OrderStatus;
import com.fiap.techChallenge.core.domain.enums.ProductStatus;
import com.fiap.techChallenge.core.gateways.order.OrderGateway;
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
@DisplayName("Testes para AddItemInOrderUseCase")
class AddItemInOrderUseCaseTest {

    @Mock
    private OrderGateway orderGateway;

    @Mock
    private ProductAvailabilityService productAvailabilityService;

    @Mock
    private Customer customer;

    @InjectMocks
    private AddItemInOrderUseCase addItemInOrderUseCase;

    @BeforeEach
    void setUp() {
        lenient().when(customer.getId()).thenReturn(UUID.randomUUID());
    }

    private Order createTestOrderWithItem(OrderStatus status, OrderItem item) {
        OrderStatusHistory initialStatus = OrderStatusHistory.build(null, status, LocalDateTime.now());
        return Order.build(
                UUID.randomUUID(),
                new ArrayList<>(List.of(item)),
                new ArrayList<>(List.of(initialStatus)),
                customer,
                LocalDateTime.now(),
                status == OrderStatus.PAGAMENTO_PENDENTE ? null : "payment123"
        );
    }

    @Test
    @DisplayName("Deve adicionar um novo item ao pedido com sucesso")
    void shouldAddNewItemToOrderSuccessfully() {
        Product existingProduct = Product.build(UUID.randomUUID(), "Existing Product", "Desc", new BigDecimal("5.0"), Category.ACOMPANHAMENTO, ProductStatus.DISPONIVEL, "img1.png");
        OrderItem existingItem = OrderItem.build(existingProduct, 1);
        Order order = createTestOrderWithItem(OrderStatus.PAGAMENTO_PENDENTE, existingItem);

        Product newProduct = Product.build(UUID.randomUUID(), "New Product", "Desc", new BigDecimal("10.0"), Category.LANCHE, ProductStatus.DISPONIVEL, "img2.png");
        int quantityToAdd = 2;

        when(orderGateway.findById(order.getId())).thenReturn(order);
        when(productAvailabilityService.findAvailableProduct(newProduct.getId())).thenReturn(newProduct);
        when(orderGateway.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Order result = addItemInOrderUseCase.execute(order.getId(), newProduct.getId(), quantityToAdd);

        assertNotNull(result);
        assertEquals(2, result.getItems().size());
        assertTrue(result.getItems().stream().anyMatch(item -> item.getProductId().equals(newProduct.getId()) && item.getQuantity() == quantityToAdd));
        verify(orderGateway).save(result);
    }

    @Test
    @DisplayName("Deve aumentar a quantidade de um item existente no pedido")
    void shouldIncreaseQuantityOfExistingItem() {
        Product product = Product.build(UUID.randomUUID(), "Product Name", "Desc", new BigDecimal("10.0"), Category.LANCHE, ProductStatus.DISPONIVEL, "image.png");
        OrderItem item = OrderItem.build(product, 1);
        Order order = createTestOrderWithItem(OrderStatus.PAGAMENTO_PENDENTE, item);
        int quantityToAdd = 2;

        when(orderGateway.findById(order.getId())).thenReturn(order);
        when(productAvailabilityService.findAvailableProduct(product.getId())).thenReturn(product);
        when(orderGateway.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Order result = addItemInOrderUseCase.execute(order.getId(), product.getId(), quantityToAdd);

        assertNotNull(result);
        assertEquals(1, result.getItems().size());
        assertEquals(3, result.getItems().get(0).getQuantity());
    }

    @Test
    @DisplayName("Deve lançar EntityNotFoundException quando o pedido não for encontrado")
    void shouldThrowEntityNotFoundExceptionWhenOrderIsNotFound() {
        UUID orderId = UUID.randomUUID();
        when(orderGateway.findById(orderId)).thenReturn(null);
        assertThrows(EntityNotFoundException.class, () -> addItemInOrderUseCase.execute(orderId, UUID.randomUUID(), 1));
        verify(productAvailabilityService, never()).findAvailableProduct(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando o produto não está disponível")
    void shouldThrowExceptionWhenProductIsNotAvailable() {
        Product product = Product.build(UUID.randomUUID(), "Product Name", "Desc", new BigDecimal("10.0"), Category.LANCHE, ProductStatus.DISPONIVEL, "image.png");
        OrderItem item = OrderItem.build(product, 1);
        Order order = createTestOrderWithItem(OrderStatus.PAGAMENTO_PENDENTE, item);

        when(orderGateway.findById(order.getId())).thenReturn(order);
        when(productAvailabilityService.findAvailableProduct(any(UUID.class))).thenThrow(new ProductNotAvaiableException());

        assertThrows(ProductNotAvaiableException.class, () -> addItemInOrderUseCase.execute(order.getId(), UUID.randomUUID(), 1));
        verify(orderGateway, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar InvalidOrderStatusException ao tentar adicionar item em pedido finalizado")
    void shouldThrowInvalidOrderStatusExceptionWhenOrderIsFinished() {
        Product product = Product.build(UUID.randomUUID(), "Product Name", "Desc", new BigDecimal("10.0"), Category.LANCHE, ProductStatus.DISPONIVEL, "image.png");
        OrderItem item = OrderItem.build(product, 1);
        Order order = createTestOrderWithItem(OrderStatus.FINALIZADO, item);

        when(orderGateway.findById(order.getId())).thenReturn(order);
        when(productAvailabilityService.findAvailableProduct(product.getId())).thenReturn(product);

        assertThrows(InvalidOrderStatusException.class, () -> addItemInOrderUseCase.execute(order.getId(), product.getId(), 1));
    }
}
