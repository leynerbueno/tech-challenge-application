package com.fiap.techChallenge.core.domain.order;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.fiap.techChallenge.core.domain.entities.order.OrderItem;
import com.fiap.techChallenge.core.domain.entities.product.Product;
import com.fiap.techChallenge.core.domain.enums.Category;
import com.fiap.techChallenge.core.domain.enums.ProductStatus;

@DisplayName("Testes para a Entidade OrderItem")
class OrderItemTest {

    private Product validProduct;

    @BeforeEach
    void setUp() {
        validProduct = Product.build(
                UUID.randomUUID(),
                "Hambúrguer de Picanha",
                "Pão brioche, hambúrguer de picanha 180g, queijo cheddar",
                new BigDecimal("35.00"),
                Category.LANCHE,
                ProductStatus.DISPONIVEL,
                "picanha_burger.png"
        );
    }

    @Nested
    @DisplayName("Testes de Criação (build)")
    class CreationTests {

        @Nested
        @DisplayName("Cenários de Sucesso")
        class Success {

            @Test
            @DisplayName("Deve criar um item a partir de um objeto Product")
            void shouldBuildFromProductObject() {
                OrderItem item = OrderItem.build(validProduct, 2);

                assertNotNull(item);
                assertEquals(validProduct.getId(), item.getProductId());
                assertEquals(validProduct.getName(), item.getProductName());
                assertEquals(2, item.getQuantity());
                assertEquals(0, validProduct.getPrice().compareTo(item.getUnitPrice()));
            }

            @Test
            @DisplayName("Deve criar um item a partir de campos individuais")
            void shouldBuildFromIndividualFields() {
                OrderItem item = OrderItem.build(
                        UUID.randomUUID(), "Batata Frita", 1, new BigDecimal("12.50"), Category.ACOMPANHAMENTO
                );
                assertNotNull(item);
                assertEquals("Batata Frita", item.getProductName());
            }
        }

        @Nested
        @DisplayName("Cenários de Falha")
        class Failure {

            @Test
            @DisplayName("Deve lançar exceção ao criar com produto nulo")
            void shouldThrowWhenProductIsNull() {
                assertThrows(IllegalArgumentException.class, () -> OrderItem.build(null, 1));
            }

            @Test
            @DisplayName("Deve lançar exceção ao criar com quantidade zero ou negativa")
            void shouldThrowWhenQuantityIsInvalid() {
                assertThrows(IllegalArgumentException.class, () -> OrderItem.build(validProduct, 0));
                assertThrows(IllegalArgumentException.class, () -> OrderItem.build(validProduct, -1));
            }

            @Test
            @DisplayName("Deve lançar exceção ao criar com preço negativo")
            void shouldThrowWhenPriceIsNegative() {
                assertThrows(IllegalArgumentException.class, ()
                        -> OrderItem.build(UUID.randomUUID(), "Item Inválido", 1, new BigDecimal("-1.0"), Category.LANCHE)
                );
            }
        }
    }

    @Nested
    @DisplayName("Testes de Modificação de Quantidade")
    class QuantityModificationTests {

        @Nested
        @DisplayName("Cenários de Sucesso")
        class Success {

            @Test
            @DisplayName("Deve aumentar a quantidade corretamente")
            void shouldIncreaseQuantity() {
                OrderItem item = OrderItem.build(validProduct, 2);
                item.increaseQuantity(3);
                assertEquals(5, item.getQuantity());
            }

            @Test
            @DisplayName("Deve diminuir a quantidade corretamente")
            void shouldDecreaseQuantity() {
                OrderItem item = OrderItem.build(validProduct, 5);
                item.decreaseQuantity(2);
                assertEquals(3, item.getQuantity());
            }
        }

        @Nested
        @DisplayName("Cenários de Falha")
        class Failure {

            @Test
            @DisplayName("Deve lançar exceção ao aumentar com quantidade inválida")
            void shouldThrowOnInvalidIncrease() {
                OrderItem item = OrderItem.build(validProduct, 2);
                assertThrows(IllegalArgumentException.class, () -> item.increaseQuantity(0));
                assertThrows(IllegalArgumentException.class, () -> item.increaseQuantity(-1));
            }

            @Test
            @DisplayName("Deve lançar exceção ao diminuir com quantidade inválida")
            void shouldThrowOnInvalidDecrease() {
                OrderItem item = OrderItem.build(validProduct, 5);
                assertThrows(IllegalArgumentException.class, () -> item.decreaseQuantity(0));
                assertThrows(IllegalArgumentException.class, () -> item.decreaseQuantity(-1));
            }

            @Test
            @DisplayName("Deve lançar exceção ao diminuir mais do que a quantidade existente")
            void shouldThrowWhenDecreasingMoreThanExists() {
                OrderItem item = OrderItem.build(validProduct, 5);
                assertThrows(IllegalArgumentException.class, () -> item.decreaseQuantity(6));
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
            @DisplayName("Deve calcular o preço total do item corretamente")
            void shouldCalculateTotalPrice() {
                // Preço unitário do produto é 35.00
                OrderItem item = OrderItem.build(validProduct, 3);
                // 3 * 35.00 = 105.00
                assertEquals(0, new BigDecimal("105.00").compareTo(item.getTotalPrice()));
            }
        }
    }
}
