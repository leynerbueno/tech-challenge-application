package com.fiap.techChallenge._webApi.data.persistence.entity.order;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ManyToOne;

import java.time.LocalDateTime;

import com.fiap.techChallenge._webApi.data.persistence.entity.user.AttendantEntity;
import com.fiap.techChallenge.core.domain.enums.OrderStatus;

@Embeddable
public class OrderStatusEmbeddable {

    @ManyToOne
    private AttendantEntity attendant;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    private LocalDateTime date;

    public OrderStatusEmbeddable() {

    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public AttendantEntity getAttendant() {
        return this.attendant;
    }

    public void setAttendant(AttendantEntity attendant) {
        this.attendant = attendant;
    }

    public LocalDateTime getDate() {
        return this.date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

}
