package com.fiap.techChallenge.core.presenter;

import java.util.List;
import java.util.stream.Collectors;

import com.fiap.techChallenge.core.application.dto.order.OrderDetailsDTO;
import com.fiap.techChallenge.core.application.dto.order.OrderResponseDTO;
import com.fiap.techChallenge.core.application.dto.order.OrderStatusViewDTO;
import com.fiap.techChallenge.core.application.dto.order.OrderSummaryDTO;
import com.fiap.techChallenge.core.application.dto.order.OrderWithItemsAndStatusDTO;
import com.fiap.techChallenge.core.application.dto.order.OrderWithStatusAndWaitMinutesDTO;
import com.fiap.techChallenge.core.application.dto.order.OrderWithStatusDTO;
import com.fiap.techChallenge.core.domain.entities.order.Order;

public class OrderPresenter {

    public static List<OrderSummaryDTO> toSummaryDTOList(List<OrderWithStatusDTO> orders) {
        return orders.stream().map(OrderPresenter::toSummaryDTO).collect(Collectors.toList());
    }

    public static List<OrderStatusViewDTO> toStatusViewDTOList(List<OrderWithStatusAndWaitMinutesDTO> orders) {
        return orders.stream().map(OrderPresenter::toStatusViewDTO).collect(Collectors.toList());
    }

    public static OrderResponseDTO toDTO(Order order) {
        return OrderResponseDTO.builder()
                .id(order.getId())
                .items(order.getItems())
                .statusHistory(order.getStatusHistory())
                .customer(order.getCustomer())
                .price(order.getPrice())
                .date(order.getDate())
                .paymentId(order.getPaymentId())
                .build();
    }

    public static OrderDetailsDTO toDetailsDTO(OrderWithItemsAndStatusDTO order) {
        return OrderDetailsDTO.builder()
                .orderId(order.orderId())
                .status(order.status())
                .statusDt(order.statusDt())
                .attendantId(order.attendantId())
                .customerId(order.customerId())
                .customerName(order.customerName())
                .price(order.price())
                .orderDt(order.orderDt())
                .paymentId(order.paymentId())
                .items(order.items())
                .build();
    }

    private static OrderSummaryDTO toSummaryDTO(OrderWithStatusDTO order) {
        return OrderSummaryDTO.builder()
                .id(order.id())
                .status(order.status())
                .statusDt(order.statusDt())
                .customerId(order.customerId())
                .customerName(order.customerName())
                .attendantId(order.attendantId())
                .attendantName(order.attendantName())
                .price(order.price())
                .orderDt(order.orderDt())
                .build();
    }

    private static OrderStatusViewDTO toStatusViewDTO(OrderWithStatusAndWaitMinutesDTO orderWithStatusAndWaitMinutesDTO) {
        return OrderStatusViewDTO.builder()
                .orderId(orderWithStatusAndWaitMinutesDTO.orderId())
                .status(orderWithStatusAndWaitMinutesDTO.status())
                .statusDt(orderWithStatusAndWaitMinutesDTO.statusDt())
                .customerId(orderWithStatusAndWaitMinutesDTO.customerId())
                .customerName(orderWithStatusAndWaitMinutesDTO.customerName())
                .attendantId(orderWithStatusAndWaitMinutesDTO.attendantId())
                .attendantName(orderWithStatusAndWaitMinutesDTO.attendantName())
                .orderDt(orderWithStatusAndWaitMinutesDTO.orderDt())
                .waitTimeMinutes(orderWithStatusAndWaitMinutesDTO.waitTimeMinutes())
                .build();
    }
}
