package com.fiap.techChallenge.core.application.useCases.order;

import com.fiap.techChallenge.core.domain.exceptions.EntityNotFoundException;
import com.fiap.techChallenge.core.domain.exceptions.order.WrongCategoryOrderException;
import com.fiap.techChallenge.core.domain.exceptions.product.ProductNotAvaiableException;
import com.fiap.techChallenge.core.application.dto.order.CreateOrderInputDTO;
import com.fiap.techChallenge.core.application.dto.order.CreateOrderItemInputDTO;
import com.fiap.techChallenge.core.application.services.order.OrderCategoryService;
import com.fiap.techChallenge.core.application.services.product.ProductAvailabilityService;
import com.fiap.techChallenge.core.domain.entities.order.Order;
import com.fiap.techChallenge.core.domain.entities.order.OrderItem;
import com.fiap.techChallenge.core.domain.entities.product.Product;
import com.fiap.techChallenge.core.domain.entities.user.customer.Customer;
import com.fiap.techChallenge.core.domain.enums.Category;
import com.fiap.techChallenge.core.domain.enums.ProductStatus;
import com.fiap.techChallenge.core.gateways.order.OrderGateway;
import com.fiap.techChallenge.core.gateways.user.CustomerGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes para CreateOrderUseCase")
class CreateOrderUseCaseTest {

    @Mock
    private OrderGateway orderGateway;
    @Mock
    private CustomerGateway customerGateway;
    @Mock
    private OrderCategoryService orderCategoryService;
    @Mock
    private ProductAvailabilityService productAvailabilityService;

    @InjectMocks
    private CreateOrderUseCase createOrderUseCase;

    private UUID customerId;
    private Product productLanche;
    private Product productBebida;

    @BeforeEach
    void setUp() {
        customerId = UUID.randomUUID();
        productLanche = Product.build(UUID.randomUUID(), "X-Burger", "Desc", new BigDecimal("25.0"), Category.LANCHE, ProductStatus.DISPONIVEL, "img.png");
        productBebida = Product.build(UUID.randomUUID(), "Refrigerante", "Desc", new BigDecimal("8.0"), Category.BEBIDA, ProductStatus.DISPONIVEL, "img.png");
    }

    @Nested
    @DisplayName("Cenários de Sucesso")
    class SuccessTests {

        @Test
        @DisplayName("Deve criar um pedido com sucesso com itens válidos")
        void shouldCreateOrderSuccessfully() {
            Customer customer = mock(Customer.class);
            when(customer.getId()).thenReturn(customerId);

            List<CreateOrderItemInputDTO> itemDTOs = List.of(
                    new CreateOrderItemInputDTO(productLanche.getId(), 1),
                    new CreateOrderItemInputDTO(productBebida.getId(), 2)
            );
            CreateOrderInputDTO inputDTO = new CreateOrderInputDTO(itemDTOs, customerId);

            when(customerGateway.findFirstById(customerId)).thenReturn(customer);
            when(productAvailabilityService.findAvailableProduct(productLanche.getId())).thenReturn(productLanche);
            when(productAvailabilityService.findAvailableProduct(productBebida.getId())).thenReturn(productBebida);
            when(orderGateway.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

            Order result = createOrderUseCase.execute(inputDTO);

            assertNotNull(result);
            assertEquals(2, result.getItems().size());
            assertEquals(0, new BigDecimal("41.0").compareTo(result.getPrice()));
            verify(orderGateway).save(any(Order.class));
        }
    }

    @Nested
    @DisplayName("Cenários de Falha")
    class FailureTests {

        @Test
        @DisplayName("Deve lançar exceção se o cliente não for encontrado")
        void shouldThrowWhenCustomerIsNotFound() {
            List<CreateOrderItemInputDTO> itemDTOs = List.of(new CreateOrderItemInputDTO(productLanche.getId(), 1));
            CreateOrderInputDTO inputDTO = new CreateOrderInputDTO(itemDTOs, customerId);

            when(productAvailabilityService.findAvailableProduct(productLanche.getId())).thenReturn(productLanche);
            doNothing().when(orderCategoryService).validateCategoryListOrder(anyList(), any(OrderItem.class));

            when(customerGateway.findFirstById(customerId)).thenReturn(null);

            assertThrows(EntityNotFoundException.class, () -> createOrderUseCase.execute(inputDTO));
            verify(orderGateway, never()).save(any(Order.class));
        }

        @Test
        @DisplayName("Deve lançar exceção se um produto não estiver disponível")
        void shouldThrowWhenProductIsUnavailable() {
            List<CreateOrderItemInputDTO> itemDTOs = List.of(new CreateOrderItemInputDTO(productLanche.getId(), 1));
            CreateOrderInputDTO inputDTO = new CreateOrderInputDTO(itemDTOs, customerId);

            when(productAvailabilityService.findAvailableProduct(productLanche.getId()))
                    .thenThrow(new ProductNotAvaiableException());

            assertThrows(ProductNotAvaiableException.class, () -> createOrderUseCase.execute(inputDTO));
        }

        @Test
        @DisplayName("Deve lançar exceção se a ordem das categorias for inválida")
        void shouldThrowWhenCategoryOrderIsInvalid() {
            List<CreateOrderItemInputDTO> itemDTOs = List.of(
                    new CreateOrderItemInputDTO(productLanche.getId(), 1),
                    new CreateOrderItemInputDTO(productBebida.getId(), 1)
            );
            CreateOrderInputDTO inputDTO = new CreateOrderInputDTO(itemDTOs, customerId);

            when(productAvailabilityService.findAvailableProduct(productLanche.getId())).thenReturn(productLanche);

            doThrow(new WrongCategoryOrderException()).when(orderCategoryService).validateCategoryListOrder(anyList(), any(OrderItem.class));
            assertThrows(WrongCategoryOrderException.class, () -> createOrderUseCase.execute(inputDTO));
        }
    }
}
