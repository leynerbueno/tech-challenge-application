package com.fiap.techChallenge.core.domain.entities.order;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import com.fiap.techChallenge.core.domain.exceptions.EntityNotFoundException;
import com.fiap.techChallenge.core.domain.exceptions.order.InvalidOrderStatusException;
import com.fiap.techChallenge.core.domain.entities.product.Product;
import com.fiap.techChallenge.core.domain.entities.user.attendant.Attendant;
import com.fiap.techChallenge.core.domain.entities.user.customer.Customer;
import com.fiap.techChallenge.core.domain.enums.OrderStatus;

public class Order {

    private final UUID id;
    private final List<OrderItem> items;
    private final Customer customer;
    private final LocalDateTime date;
    private final List<OrderStatusHistory> statusHistory;
    private String paymentId;

    private Order(UUID id, List<OrderItem> items, List<OrderStatusHistory> statusHistory, Customer customer, LocalDateTime date, String paymentId) {
        this.id = id;
        this.items = items;
        this.statusHistory = statusHistory;
        this.customer = customer;
        this.date = date;
        this.paymentId = paymentId;
    }

    public UUID getId() {
        return this.id;
    }

    public Customer getCustomer() {
        return customer;
    }

    public LocalDateTime getDate() {
        return this.date;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public List<OrderItem> getItems() {
        return Collections.unmodifiableList(this.items);
    }

    public List<OrderStatusHistory> getStatusHistory() {
        return Collections.unmodifiableList(this.statusHistory);
    }

    public BigDecimal getPrice() {
        if (this.items == null) {
            return BigDecimal.ZERO;
        }
        return items.stream()
                .map(OrderItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public OrderStatus getCurrentStatus() {
        if (this.statusHistory == null || this.statusHistory.isEmpty()) {
            throw new IllegalStateException("O pedido não possui histórico de status.");
        }
        return this.statusHistory.stream()
                .max(Comparator.comparing(OrderStatusHistory::getDate))
                .get().getStatus();
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public void addItem(Product product, int quantity) {
        OrderStatus currentStatus = this.getCurrentStatus();

        if (currentStatus == OrderStatus.CANCELADO
                || currentStatus == OrderStatus.FINALIZADO) {
            throw new InvalidOrderStatusException("Não é possivel adicionar um item ao pedido, pois ele está " + currentStatus);
        }

        this.items.stream()
                .filter(item -> item.getProductId().equals(product.getId()))
                .findFirst()
                .ifPresentOrElse(
                        existingItem -> existingItem.increaseQuantity(quantity),
                        () -> this.items.add(OrderItem.build(product, quantity))
                );
    }

    public void removeItem(UUID productId, int quantity) {
        if (this.getCurrentStatus() != OrderStatus.PAGAMENTO_PENDENTE) {
            throw new IllegalStateException("Não é possível remover itens de um pedido que já progrediu no pagamento.");
        }

        OrderItem itemToRemove = this.items.stream()
                .filter(item -> item.getProductId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Product"));

        itemToRemove.decreaseQuantity(quantity);

        if (itemToRemove.getQuantity() == 0) {
            this.items.remove(itemToRemove);
            if (this.items.size() <= 1) {
                throw new IllegalStateException("O pedido não pode ter menos de um item.");
            }
        }
    }

    public void moveStatusToPaid() {
        validateTransition(OrderStatus.PAGAMENTO_PENDENTE);
        addStatus(OrderStatus.PAGO, null);
    }

    public void moveStatusToReceived(Attendant attendant) {
        validateTransition(OrderStatus.PAGO);
        addStatus(OrderStatus.RECEBIDO, attendant);
    }

    public void moveStatusToInPreparation(Attendant attendant) {
        validateTransition(OrderStatus.RECEBIDO);
        addStatus(OrderStatus.EM_PREPARACAO, attendant);
    }

    public void moveStatusToReady(Attendant attendant) {
        validateTransition(OrderStatus.EM_PREPARACAO);
        addStatus(OrderStatus.PRONTO, attendant);
    }

    public void moveStatusToFinished(Attendant attendant) {
        validateTransition(OrderStatus.PRONTO);
        addStatus(OrderStatus.FINALIZADO, attendant);
    }

    public void moveStatusToCanceled(Attendant attendant) {
        addStatus(OrderStatus.CANCELADO, attendant);
    }

    private void validateTransition(OrderStatus expectedCurrentStatus) {
        OrderStatus currentStatus = this.getCurrentStatus();
        if (currentStatus == OrderStatus.FINALIZADO || currentStatus == OrderStatus.CANCELADO) {
            throw new InvalidOrderStatusException("Não é possível alterar status de um pedido que já foi finalizado ou cancelado.");

        } else if (currentStatus != expectedCurrentStatus) {
            throw new InvalidOrderStatusException(
                    "Transição de status inválida. Esperado: " + expectedCurrentStatus
                    + ", mas o status atual é: " + this.getCurrentStatus()
            );
        }
    }

    private void addStatus(OrderStatus newStatus, Attendant attendant) {
        boolean alreadyExists = this.statusHistory.stream()
                .anyMatch(history -> history.getStatus() == newStatus);
        if (alreadyExists) {
            throw new InvalidOrderStatusException("O status " + newStatus + " já foi aplicado a este pedido.");
        }
        this.statusHistory.add(OrderStatusHistory.build(attendant, newStatus, LocalDateTime.now()));
    }

    public static Order build(UUID id, List<OrderItem> items, List<OrderStatusHistory> statusHistory, Customer customer, LocalDateTime date, String paymentId) {
        validate(items, statusHistory, customer, date);
        return new Order(id, new ArrayList<>(items), new ArrayList<>(statusHistory), customer, date, paymentId);
    }

    private static void validate(List<OrderItem> items, List<OrderStatusHistory> statusHistory, Customer customer, LocalDateTime date) {
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("Os itens do pedido precisam ser preenchidos");
        }
        if (statusHistory == null || statusHistory.isEmpty()) {
            throw new IllegalArgumentException("O histórico de status do pedido precisa ser preenchido");
        }
        if (customer == null || customer.getId() == null) {
            throw new IllegalArgumentException("O cliente do pedido precisa ser preenchido");
        }
        if (date == null) {
            throw new IllegalArgumentException("A data do pedido precisa ser preenchida");
        }
    }
}
