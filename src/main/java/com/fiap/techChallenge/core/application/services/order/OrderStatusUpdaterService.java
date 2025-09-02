package com.fiap.techChallenge.core.application.services.order;

import com.fiap.techChallenge.core.domain.entities.order.Order;
import com.fiap.techChallenge.core.domain.entities.user.attendant.Attendant;
import com.fiap.techChallenge.core.domain.exceptions.EntityNotFoundException;
import com.fiap.techChallenge.core.gateways.notification.EmailNotificationGateway;
import com.fiap.techChallenge.core.gateways.order.OrderGateway;
import com.fiap.techChallenge.core.gateways.user.AttendantGateway;

import java.util.UUID;

import com.fiap.techChallenge.core.domain.enums.OrderStatus;

public class OrderStatusUpdaterService {

    private final OrderGateway orderGateway;
    private final AttendantGateway attendantGateway;
    private final EmailNotificationGateway emailGateway;

    public OrderStatusUpdaterService(OrderGateway orderGateway, AttendantGateway attendantGateway, EmailNotificationGateway emailGateway) {
        this.orderGateway = orderGateway;
        this.attendantGateway = attendantGateway;
        this.emailGateway = emailGateway;
    }

    public Order moveStatusToPaid(UUID orderId) {
        Order order = findOrder(orderId);
        order.moveStatusToPaid();

        return orderGateway.save(order);
    }

    public Order moveStatusToReceived(UUID orderId, UUID attendantId) {
        Order order = findOrder(orderId);
        Attendant attendant = findAttendant(attendantId);
        order.moveStatusToReceived(attendant);
        Order savedOrder = orderGateway.save(order);

        sendNotification(order.getCustomer().getEmail(), orderId, order.getCurrentStatus());

        return savedOrder;
    }

    public Order moveStatusToInPreparation(UUID orderId, UUID attendantId) {
        Order order = findOrder(orderId);
        Attendant attendant = findAttendant(attendantId);
        order.moveStatusToInPreparation(attendant);
        Order savedOrder = orderGateway.save(order);

        sendNotification(order.getCustomer().getEmail(), orderId, order.getCurrentStatus());

        return savedOrder;
    }

    public Order moveStatusToReady(UUID orderId, UUID attendantId) {
        Order order = findOrder(orderId);
        Attendant attendant = findAttendant(attendantId);
        order.moveStatusToReady(attendant);
        Order savedOrder = orderGateway.save(order);

        sendNotification(order.getCustomer().getEmail(), orderId, order.getCurrentStatus());

        return savedOrder;
    }

    public Order moveStatusToFinished(UUID orderId, UUID attendantId) {
        Order order = findOrder(orderId);
        Attendant attendant = findAttendant(attendantId);
        order.moveStatusToFinished(attendant);
        Order savedOrder = orderGateway.save(order);

        sendNotification(order.getCustomer().getEmail(), orderId, order.getCurrentStatus());

        return savedOrder;
    }

    public Order moveStatusToCanceled(UUID orderId, UUID attendantId) {
        Order order = findOrder(orderId);
        Attendant attendant = findAttendant(attendantId);
        order.moveStatusToCanceled(attendant);
        return orderGateway.save(order);
    }

    private void sendNotification(String customerEmail, UUID orderId, OrderStatus status) {
        try {
            emailGateway.sendEmail(customerEmail, orderId, status);
        } catch (Exception e) {

        }
    }

    private Order findOrder(UUID orderId) {
        Order order = orderGateway.findById(orderId);

        if (order == null) {
            throw new EntityNotFoundException("Order");

        }

        return order;
    }

    private Attendant findAttendant(UUID attendantId) {
        if (attendantId == null) {
            return null;
        }

        Attendant attendant = attendantGateway.findFirstById(attendantId);

        if (attendant == null) {
            throw new EntityNotFoundException("Attendant");
        }
        return attendant;
    }
}
