package com.fiap.techChallenge._webApi.data.persistence.entity.order;

import java.math.BigDecimal;
import java.util.UUID;

import com.fiap.techChallenge.core.domain.enums.Category;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

@Embeddable
public class OrderItemEmbeddable {

    private UUID productId;
    private String productName;
    private Integer quantity;
    private BigDecimal unitPrice;

    @Enumerated(EnumType.STRING)
    private Category category;

    public OrderItemEmbeddable() {
    }

    public UUID getProductId() {
        return this.productId;
    }

    public void setProductId(UUID productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return this.productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Integer getQuantity() {
        return this.quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getUnitPrice() {
        return this.unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public Category getCategory() {
        return this.category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

}
