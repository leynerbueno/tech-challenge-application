package com.fiap.techChallenge.core.domain.entities.order;

import java.time.LocalDateTime;

import com.fiap.techChallenge.core.domain.entities.user.attendant.Attendant;
import com.fiap.techChallenge.core.domain.enums.OrderStatus;

public class OrderStatusHistory {

    private Attendant attendant;

    private OrderStatus status;

    private LocalDateTime date;

    public OrderStatusHistory() {

    }

    private OrderStatusHistory(Attendant attendant, OrderStatus status, LocalDateTime date) {
        this.attendant = attendant;
        this.status = status;
        this.date = date;
    }

    public Attendant getAttendant() {
        return this.attendant;
    }

    public void setAttendantId(Attendant attendant) {
        this.attendant = attendant;
    }

    public OrderStatus getStatus() {
        return this.status;
    }

    public void setStatus(OrderStatus status) {
        if (status == null) {
            throw new IllegalArgumentException("O status deve ser preenchido");
        }
        this.status = status;
    }

    public LocalDateTime getDate() {
        return this.date;
    }

    public void setDate(LocalDateTime date) {
        if (date == null) {
            throw new IllegalArgumentException("A data deve ser preenchida");
        }
        this.date = date;
    }

    public static OrderStatusHistory build(Attendant attendant, OrderStatus status, LocalDateTime date) {
        validate(status, date);
        return new OrderStatusHistory(attendant, status, date);
    }

    private static void validate(OrderStatus status, LocalDateTime date) {
        if (status == null) {
            throw new IllegalArgumentException("O status deve ser preenchido");
        }
        if (date == null) {
            throw new IllegalArgumentException("A data deve ser preenchida");
        }
    }

}
