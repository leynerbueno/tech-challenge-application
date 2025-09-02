package com.fiap.techChallenge.core.application.services.order;

import com.fiap.techChallenge.core.domain.entities.order.OrderItem;
import com.fiap.techChallenge.core.domain.enums.Category;
import com.fiap.techChallenge.core.domain.exceptions.order.WrongCategoryOrderException;
import com.fiap.techChallenge.core.gateways.product.ProductGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes para OrderCategoryService")
class OrderCategoryServiceTest {

    @Mock
    private ProductGateway productGateway;

    @InjectMocks
    private OrderCategoryService orderCategoryService;

    private OrderItem lancheItem;
    private OrderItem acompanhamentoItem;
    private OrderItem bebidaItem;
    private OrderItem sobremesaItem;
    
    private List<Category> allProductCategories;
    private List<Category> categoriesWithoutSobremesa;


    @BeforeEach
    void setUp() {
        allProductCategories = List.of(
            Category.LANCHE,
            Category.ACOMPANHAMENTO,
            Category.BEBIDA,
            Category.SOBREMESA
        );
        
        categoriesWithoutSobremesa = List.of(
            Category.LANCHE,
            Category.ACOMPANHAMENTO,
            Category.BEBIDA
        );

        lancheItem = mock(OrderItem.class);
        acompanhamentoItem = mock(OrderItem.class);
        bebidaItem = mock(OrderItem.class);
        sobremesaItem = mock(OrderItem.class);
    }

    @Nested
    @DisplayName("Cenários de Sucesso")
    class SuccessTests {

        @Test
        @DisplayName("Deve permitir adicionar qualquer item válido a um pedido vazio")
        void shouldAllowAddingAnyItemToEmptyOrder() {
            when(productGateway.listAvailableCategorys()).thenReturn(allProductCategories);
            when(lancheItem.getCategory()).thenReturn(Category.LANCHE);
            List<OrderItem> currentItems = Collections.emptyList();

            assertDoesNotThrow(() ->
                orderCategoryService.validateCategoryListOrder(currentItems, lancheItem)
            );
        }

        @Test
        @DisplayName("Deve permitir adicionar um item de categoria igual ou posterior à mais alta no pedido")
        void shouldAllowAddingItemOfSameOrLaterCategory() {
            when(productGateway.listAvailableCategorys()).thenReturn(allProductCategories);
            when(lancheItem.getCategory()).thenReturn(Category.LANCHE);
            when(acompanhamentoItem.getCategory()).thenReturn(Category.ACOMPANHAMENTO);
            when(bebidaItem.getCategory()).thenReturn(Category.BEBIDA);

            List<OrderItem> currentItems = List.of(lancheItem, acompanhamentoItem);

            assertDoesNotThrow(() ->
                orderCategoryService.validateCategoryListOrder(currentItems, acompanhamentoItem)
            );
            
            assertDoesNotThrow(() ->
                orderCategoryService.validateCategoryListOrder(currentItems, bebidaItem)
            );
        }
    }

    @Nested
    @DisplayName("Cenários de Falha")
    class FailureTests {
        
        @Test
        @DisplayName("Deve lançar exceção ao adicionar um item de categoria indisponível")
        void shouldThrowWhenAddingItemOfUnavailableCategory() {
            when(productGateway.listAvailableCategorys()).thenReturn(categoriesWithoutSobremesa);

            when(sobremesaItem.getCategory()).thenReturn(Category.SOBREMESA);
            List<OrderItem> currentItems = List.of(lancheItem);
            
            assertThrows(WrongCategoryOrderException.class, () ->
                orderCategoryService.validateCategoryListOrder(currentItems, sobremesaItem)
            );
        }

        @Test
        @DisplayName("Deve lançar exceção ao adicionar um item de categoria anterior à mais alta no pedido")
        void shouldThrowWhenAddingItemOfEarlierCategory() {
            when(productGateway.listAvailableCategorys()).thenReturn(allProductCategories);
            when(bebidaItem.getCategory()).thenReturn(Category.BEBIDA);
            when(lancheItem.getCategory()).thenReturn(Category.LANCHE);
            
            List<OrderItem> currentItems = List.of(bebidaItem);

            assertThrows(WrongCategoryOrderException.class, () ->
                orderCategoryService.validateCategoryListOrder(currentItems, lancheItem)
            );
        }

        @Test
        @DisplayName("Deve lançar exceção ao adicionar um acompanhamento após uma sobremesa")
        void shouldThrowWhenAddingAccompanimentAfterDessert() {
            when(productGateway.listAvailableCategorys()).thenReturn(allProductCategories);
            when(sobremesaItem.getCategory()).thenReturn(Category.SOBREMESA);
            when(acompanhamentoItem.getCategory()).thenReturn(Category.ACOMPANHAMENTO);

            List<OrderItem> currentItems = List.of(sobremesaItem);
            
            assertThrows(WrongCategoryOrderException.class, () ->
                orderCategoryService.validateCategoryListOrder(currentItems, acompanhamentoItem)
            );
        }
    }
}