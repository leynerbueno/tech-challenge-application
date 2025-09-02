package com.fiap.techChallenge.core.gateways.order;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import com.fiap.techChallenge._webApi.mappers.OrderMapper;
import com.fiap.techChallenge.core.application.dto.order.OrderDTO;
import com.fiap.techChallenge.core.application.dto.order.OrderItemDTO;
import com.fiap.techChallenge.core.application.dto.order.OrderWithItemsAndStatusDTO;
import com.fiap.techChallenge.core.application.dto.order.OrderWithStatusAndWaitMinutesDTO;
import com.fiap.techChallenge.core.application.dto.order.OrderWithStatusDTO;
import com.fiap.techChallenge.core.application.dto.order.projection.OrderWithItemsAndStatusProjection;
import com.fiap.techChallenge.core.application.dto.order.projection.OrderWithStatusProjection;
import com.fiap.techChallenge.core.domain.entities.order.Order;
import com.fiap.techChallenge.core.interfaces.CompositeDataSource;

public class OrderGatewayImpl implements OrderGateway {

    private final CompositeDataSource dataSource;

    public OrderGatewayImpl(CompositeDataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Order save(Order order) {
        OrderDTO dto = OrderMapper.toDTO(order);
        dto = dataSource.saveOrder(dto);

        return OrderMapper.toDomain(dto);
    }

    @Override
    public Order findById(UUID id) {
        OrderDTO dto = dataSource.findOrderById(id);

        if (dto == null) {
            return null;
        }

        return OrderMapper.toDomain(dto);
    }

    @Override
    public Optional<OrderWithItemsAndStatusDTO> findWithDetailsById(UUID id) {
        Optional<OrderWithItemsAndStatusProjection> optionalOrder = dataSource.findOrderWithDetailsById(id);

        return optionalOrder.map(orderProjection -> {
            List<OrderItemDTO> items = OrderMapper.parseItemsJson(orderProjection.getItemsJson());

            return OrderWithItemsAndStatusDTO.builder()
                    .orderId(orderProjection.getOrderId())
                    .status(orderProjection.getStatus())
                    .statusDt(orderProjection.getStatusDt())
                    .attendantId(orderProjection.getAttendantId())
                    .attendantName(orderProjection.getAttendantName())
                    .customerId(orderProjection.getCustomerId())
                    .customerName(orderProjection.getCustomerName())
                    .price(orderProjection.getPrice())
                    .orderDt(orderProjection.getOrderDt())
                    .paymentId(orderProjection.getPaymentId())
                    .items(items)
                    .build();
        });
    }

    @Override
    public List<OrderWithStatusDTO> listByPeriod(LocalDateTime initialDt, LocalDateTime finalDt) {
        List<OrderWithStatusProjection> projections = dataSource.listOrderByPeriod(initialDt, finalDt);

        return projections.stream().map(projection -> {
            return OrderWithStatusDTO.builder()
                    .id(projection.getOrderId())
                    .status(projection.getStatus())
                    .statusDt(projection.getStatusDt())
                    .customerId(projection.getCustomerId())
                    .customerName(projection.getCustomerName())
                    .attendantId(projection.getAttendantId())
                    .attendantName(projection.getAttendantName())
                    .price(projection.getPrice())
                    .orderDt(projection.getOrderDt())
                    .build();
        })
                .collect(Collectors.toList());
    }

    @Override
    public List<OrderWithStatusAndWaitMinutesDTO> listTodayOrders(List<String> statusList, int finalizedMinutes) {
        return OrderMapper.toDtoListFromProjection(dataSource.listTodayOrders(statusList, finalizedMinutes));
    }

    @Override
    public Order findByPaymentId(String paymentId) {
        OrderDTO dto = dataSource.findOrderIdByPaymentId(paymentId);

        if (dto == null) {
            return null;
        }

        return OrderMapper.toDomain(dto);
    }

    @Override
    public void updateOrderIdPayment(UUID orderId, String paymentId) {
        OrderDTO orderdto = dataSource.findOrderById(orderId);

        Order order = OrderMapper.toDomain(orderdto);
        order.setPaymentId(paymentId);
        dataSource.saveOrder(OrderMapper.toDTO(order));
    }
}
