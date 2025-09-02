package com.fiap.techChallenge._webApi.mappers;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fiap.techChallenge._webApi.dto.order.CreateOrderDTO;
import com.fiap.techChallenge._webApi.dto.order.CreateOrderItemDTO;
import com.fiap.techChallenge._webApi.data.persistence.entity.order.OrderEntity;
import com.fiap.techChallenge._webApi.data.persistence.entity.order.OrderItemEmbeddable;
import com.fiap.techChallenge._webApi.data.persistence.entity.order.OrderStatusEmbeddable;
import com.fiap.techChallenge._webApi.data.persistence.entity.user.CustomerEntity;
import com.fiap.techChallenge.core.application.dto.order.CreateOrderInputDTO;
import com.fiap.techChallenge.core.application.dto.order.CreateOrderItemInputDTO;
import com.fiap.techChallenge.core.application.dto.order.OrderDTO;
import com.fiap.techChallenge.core.application.dto.order.OrderItemDTO;
import com.fiap.techChallenge.core.application.dto.order.OrderStatusHistoryDTO;
import com.fiap.techChallenge.core.application.dto.order.OrderWithStatusAndWaitMinutesDTO;
import com.fiap.techChallenge.core.application.dto.order.projection.OrderWithStatusAndWaitMinutesProjection;
import com.fiap.techChallenge.core.application.dto.user.CustomerFullDTO;
import com.fiap.techChallenge.core.domain.entities.order.Order;
import com.fiap.techChallenge.core.domain.entities.order.OrderItem;
import com.fiap.techChallenge.core.domain.entities.order.OrderStatusHistory;

public class OrderMapper {

    private static final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    public static CreateOrderInputDTO toCreateOrderInputDTO(CreateOrderDTO requestDTO) {
        List<CreateOrderItemInputDTO> items = requestDTO.items().stream().map(OrderMapper::toCreateOrderItemInputDTO).collect(Collectors.toList());

        return CreateOrderInputDTO.builder()
                .customerId(requestDTO.customerId())
                .items(items)
                .build();
    }

    public static CreateOrderItemInputDTO toCreateOrderItemInputDTO(CreateOrderItemDTO requestDTO) {
        return CreateOrderItemInputDTO.builder()
                .quantity(requestDTO.quantity())
                .productId(requestDTO.productId())
                .build();
    }

    public static List<OrderWithStatusAndWaitMinutesDTO> toDtoListFromProjection(List<OrderWithStatusAndWaitMinutesProjection> ordersProjectionList) {
        return ordersProjectionList.stream().map(OrderMapper::toDtoFromProjection).collect(Collectors.toList());
    }

    public static Order toDomain(OrderDTO dto) {
        if (dto == null) {
            return null;
        }

        return Order.build(
                dto.id(),
                toItemDomainList(dto.items()),
                toStatusHistoryDomainList(dto.statusHistory()),
                CustomerMapper.customerDtoToDomain(dto.customer()),
                dto.date(),
                dto.paymentId());
    }

    public static OrderDTO toDTO(Order order) {
        if (order == null) {
            return null;
        }

        CustomerFullDTO customerDTO = CustomerMapper.customerDomainToDto(order.getCustomer());
        List<OrderItemDTO> itemDTOs = toItemDTOList(order.getItems());
        List<OrderStatusHistoryDTO> statusDTOs = toStatusHistoryDTOList(order.getStatusHistory());

        return OrderDTO.builder()
                .id(order.getId())
                .items(itemDTOs)
                .statusHistory(statusDTOs)
                .customer(customerDTO)
                .price(order.getPrice())
                .date(order.getDate())
                .paymentId(order.getPaymentId())
                .build();
    }

    public static OrderDTO toDTO(OrderEntity entity) {
        if (entity == null) {
            return null;
        }

        List<OrderItemDTO> items = toItemDTOFromEntityList(entity.getItems());
        List<OrderStatusHistoryDTO> statusHistory = toStatusHistoryDTOFromEntityList(entity.getStatusHistory());
        CustomerFullDTO customerDTO = CustomerMapper.customerEntityToDto(entity.getCustomer());

        return OrderDTO.builder()
                .id(entity.getId())
                .items(items)
                .statusHistory(statusHistory)
                .customer(customerDTO)
                .price(entity.getPrice())
                .date(entity.getDate())
                .paymentId(entity.getPaymentId())
                .build();
    }

    public static OrderEntity toEntity(OrderDTO dto) {
        if (dto == null) {
            return null;
        }

        List<OrderItemEmbeddable> items = toItemEmbeddableList(dto.items());
        List<OrderStatusEmbeddable> statusHistory = toStatusEmbeddableList(dto.statusHistory());
        CustomerEntity customer = CustomerMapper.customerDtoToEntity(dto.customer());

        OrderEntity entity = new OrderEntity(
                dto.id(),
                items,
                statusHistory,
                customer,
                dto.price(),
                dto.date(),
                dto.paymentId()
        );
        return entity;
    }

    public static List<OrderItemDTO> parseItemsJson(String json) {
        if (json == null) {
            return Collections.emptyList();
        }
        try {
            CollectionType listType = objectMapper.getTypeFactory()
                    .constructCollectionType(List.class, OrderItemDTO.class);
            return objectMapper.readValue(json, listType);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    private static OrderWithStatusAndWaitMinutesDTO toDtoFromProjection(OrderWithStatusAndWaitMinutesProjection projection) {
        return OrderWithStatusAndWaitMinutesDTO.builder()
                .orderId(projection.getOrderId())
                .status(projection.getStatus())
                .statusDt(projection.getStatusDt())
                .customerId(projection.getCustomerId())
                .customerName(projection.getCustomerName())
                .attendantId(projection.getAttendantId())
                .attendantName(projection.getAttendantName())
                .orderDt(projection.getOrderDt())
                .waitTimeMinutes(projection.getWaitTimeMinutes())
                .build();
    }

    private static List<OrderStatusEmbeddable> toStatusEmbeddableList(List<OrderStatusHistoryDTO> statusHistory) {
        return statusHistory.stream()
                .map(s -> {
                    OrderStatusEmbeddable status = new OrderStatusEmbeddable();
                    status.setAttendant(AttendantMapper.dtoToEntity(s.attendant()));
                    status.setStatus(s.status());
                    status.setDate(s.date());
                    return status;
                }).collect(Collectors.toList());
    }

    private static List<OrderItemEmbeddable> toItemEmbeddableList(List<OrderItemDTO> items) {
        return items.stream()
                .map(i -> {
                    OrderItemEmbeddable item = new OrderItemEmbeddable();
                    item.setProductId(i.productId());
                    item.setProductName(i.productName());
                    item.setQuantity(i.quantity());
                    item.setUnitPrice(i.unitPrice());
                    item.setCategory(i.category());
                    return item;
                }).collect(Collectors.toList());
    }

    private static OrderItem toItemDomain(OrderItemDTO item) {
        return OrderItem.build(
                item.productId(),
                item.productName(),
                item.quantity(),
                item.unitPrice(),
                item.category()
        );
    }

    private static OrderItemDTO toItemDTO(OrderItem item) {
        return OrderItemDTO.builder()
                .productId(item.getProductId())
                .productName(item.getProductName())
                .quantity(item.getQuantity())
                .unitPrice(item.getUnitPrice())
                .category(item.getCategory())
                .build();
    }

    private static OrderItemDTO toItemDTOFromEntity(OrderItemEmbeddable item) {
        return OrderItemDTO.builder()
                .productId(item.getProductId())
                .productName(item.getProductName())
                .quantity(item.getQuantity())
                .unitPrice(item.getUnitPrice())
                .category(item.getCategory())
                .build();
    }

    private static List<OrderItemDTO> toItemDTOList(List<OrderItem> items) {
        return items.stream().map(OrderMapper::toItemDTO).collect(Collectors.toList());
    }

    private static List<OrderItemDTO> toItemDTOFromEntityList(List<OrderItemEmbeddable> items) {
        return items.stream().map(OrderMapper::toItemDTOFromEntity).collect(Collectors.toList());
    }

    private static List<OrderItem> toItemDomainList(List<OrderItemDTO> items) {
        return items.stream().map(OrderMapper::toItemDomain).collect(Collectors.toList());
    }

    private static OrderStatusHistoryDTO toStatusHistoryDTO(OrderStatusHistory statusHistory) {
        return OrderStatusHistoryDTO.builder()
                .attendant(AttendantMapper.domainToDto(statusHistory.getAttendant()))
                .status(statusHistory.getStatus())
                .date(statusHistory.getDate())
                .build();
    }

    private static OrderStatusHistory toStatusHistoryDomain(OrderStatusHistoryDTO dto) {
        return OrderStatusHistory.build(
                AttendantMapper.DtoToDomain(dto.attendant()),
                dto.status(),
                dto.date()
        );
    }

    private static OrderStatusHistoryDTO toStatusHistoryDTOFromEntity(OrderStatusEmbeddable statusHistory) {
        return OrderStatusHistoryDTO.builder()
                .attendant(AttendantMapper.entityToDto(statusHistory.getAttendant()))
                .status(statusHistory.getStatus())
                .date(statusHistory.getDate())
                .build();
    }

    private static List<OrderStatusHistoryDTO> toStatusHistoryDTOList(List<OrderStatusHistory> statusHistory) {
        return statusHistory.stream().map(OrderMapper::toStatusHistoryDTO).collect(Collectors.toList());
    }

    private static List<OrderStatusHistoryDTO> toStatusHistoryDTOFromEntityList(List<OrderStatusEmbeddable> statusHistory) {
        return statusHistory.stream().map(OrderMapper::toStatusHistoryDTOFromEntity).collect(Collectors.toList());
    }

    private static List<OrderStatusHistory> toStatusHistoryDomainList(List<OrderStatusHistoryDTO> statusHistory) {
        return statusHistory.stream().map(OrderMapper::toStatusHistoryDomain).collect(Collectors.toList());
    }
}
