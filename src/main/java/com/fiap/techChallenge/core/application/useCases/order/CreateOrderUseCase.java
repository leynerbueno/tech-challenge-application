package com.fiap.techChallenge.core.application.useCases.order;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.fiap.techChallenge.core.domain.exceptions.EntityNotFoundException;
import com.fiap.techChallenge.core.application.dto.order.CreateOrderInputDTO;
import com.fiap.techChallenge.core.application.dto.order.CreateOrderItemInputDTO;
import com.fiap.techChallenge.core.application.services.order.OrderCategoryService;
import com.fiap.techChallenge.core.application.services.product.ProductAvailabilityService;
import com.fiap.techChallenge.core.domain.entities.order.Order;
import com.fiap.techChallenge.core.domain.entities.order.OrderItem;
import com.fiap.techChallenge.core.domain.entities.order.OrderStatusHistory;
import com.fiap.techChallenge.core.domain.entities.product.Product;
import com.fiap.techChallenge.core.domain.entities.user.customer.Customer;
import com.fiap.techChallenge.core.domain.enums.OrderStatus;
import com.fiap.techChallenge.core.gateways.order.OrderGateway;
import com.fiap.techChallenge.core.gateways.user.CustomerGateway;

public class CreateOrderUseCase {

    private final OrderGateway orderGateway;
    private final CustomerGateway customerGateway;
    private final OrderCategoryService orderCategoryService;
    private final ProductAvailabilityService productAvailabilityService;

    public CreateOrderUseCase(OrderGateway orderGateway, CustomerGateway customerGateway, OrderCategoryService orderCategoryService, ProductAvailabilityService productAvailabilityService) {
        this.orderGateway = orderGateway;
        this.customerGateway = customerGateway;
        this.orderCategoryService = orderCategoryService;
        this.productAvailabilityService = productAvailabilityService;
    }

    public Order execute(CreateOrderInputDTO dto) {
        List<OrderItem> items = processOrderItems(dto.items());

        Order order = Order.build(
                null,
                items,
                createInitialStatusHistory(),
                validateCustomer(dto.customerId()),
                LocalDateTime.now(),
                null
        );

        return orderGateway.save(order);
    }

    private List<OrderStatusHistory> createInitialStatusHistory() {
        List<OrderStatusHistory> statusHistory = new ArrayList<>();

        OrderStatusHistory status = OrderStatusHistory.build(
                null,
                OrderStatus.PAGAMENTO_PENDENTE,
                LocalDateTime.now()
        );

        statusHistory.add(status);
        return statusHistory;
    }

    private Customer validateCustomer(UUID customerId) {
        Customer customer = customerGateway.findFirstById(customerId);

        if (customer == null || customer.getId() == null) {
            throw new EntityNotFoundException("Customer");
        }

        return customer;
    }

    private List<OrderItem> processOrderItems(List<CreateOrderItemInputDTO> itemDTOs) {
        Map<UUID, Integer> quantitiesPerProduct = new LinkedHashMap<>();

        for (CreateOrderItemInputDTO dto : itemDTOs) {
            quantitiesPerProduct.merge(dto.productId(), dto.quantity(), (oldQuantity, newQuantity) -> oldQuantity + newQuantity);
        }

        List<OrderItem> items = new ArrayList<>();

        for (Map.Entry<UUID, Integer> entry : quantitiesPerProduct.entrySet()) {
            UUID productId = entry.getKey();
            Integer totalQuantity = entry.getValue();

            Product product = productAvailabilityService.findAvailableProduct(productId);

            OrderItem newItem = OrderItem.build(
                    product.getId(),
                    product.getName(),
                    totalQuantity,
                    product.getPrice(),
                    product.getCategory()
            );

            orderCategoryService.validateCategoryListOrder(items, newItem);
            items.add(newItem);
        }

        return items;
    }

}
