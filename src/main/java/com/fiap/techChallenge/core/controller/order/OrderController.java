package com.fiap.techChallenge.core.controller.order;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.mail.javamail.JavaMailSender;

import com.fiap.techChallenge.core.application.dto.order.CreateOrderInputDTO;
import com.fiap.techChallenge.core.application.dto.order.OrderDetailsDTO;
import com.fiap.techChallenge.core.application.dto.order.OrderResponseDTO;
import com.fiap.techChallenge.core.application.dto.order.OrderStatusViewDTO;
import com.fiap.techChallenge.core.application.dto.order.OrderSummaryDTO;
import com.fiap.techChallenge.core.application.dto.order.UpdateOrderStatusInputDTO;
import com.fiap.techChallenge.core.application.services.order.OrderCategoryService;
import com.fiap.techChallenge.core.application.services.order.OrderStatusUpdaterService;
import com.fiap.techChallenge.core.application.services.product.ProductAvailabilityService;
import com.fiap.techChallenge.core.application.useCases.order.AddItemInOrderUseCase;
import com.fiap.techChallenge.core.application.useCases.order.CreateOrderUseCase;
import com.fiap.techChallenge.core.application.useCases.order.FindOrderByIdUseCase;
import com.fiap.techChallenge.core.application.useCases.order.ListOrdersByPeriodUseCase;
import com.fiap.techChallenge.core.application.useCases.order.ListTodayOrdersUseCase;
import com.fiap.techChallenge.core.application.useCases.order.RemoveItemFromOrderUseCase;
import com.fiap.techChallenge.core.application.useCases.order.UpdateOrderStatusUseCase;
import com.fiap.techChallenge.core.gateways.notification.EmailNotificationGateway;
import com.fiap.techChallenge.core.gateways.notification.EmailNotificationGatewayImpl;
import com.fiap.techChallenge.core.gateways.order.OrderGateway;
import com.fiap.techChallenge.core.gateways.order.OrderGatewayImpl;
import com.fiap.techChallenge.core.gateways.product.ProductGateway;
import com.fiap.techChallenge.core.gateways.product.ProductGatewayImpl;
import com.fiap.techChallenge.core.gateways.user.AttendantGateway;
import com.fiap.techChallenge.core.gateways.user.AttendantGatewayImpl;
import com.fiap.techChallenge.core.gateways.user.CustomerGateway;
import com.fiap.techChallenge.core.gateways.user.CustomerGatewayImpl;
import com.fiap.techChallenge.core.interfaces.CompositeDataSource;
import com.fiap.techChallenge.core.presenter.OrderPresenter;

public class OrderController {

    private final OrderGateway orderGateway;
    private final ProductGateway productGateway;
    private final CustomerGateway customerGateway;
    private final OrderCategoryService orderCategoryService;
    private final ProductAvailabilityService productAvailabilityService;
    private final OrderStatusUpdaterService orderStatusUpdaterService;

    private OrderController(CompositeDataSource compositeDataSource, JavaMailSender javaMailSender, String mailFrom) {
        this.orderGateway = new OrderGatewayImpl(compositeDataSource);
        this.productGateway = new ProductGatewayImpl(compositeDataSource);
        this.customerGateway = new CustomerGatewayImpl(compositeDataSource);
        AttendantGateway attendantGateway = new AttendantGatewayImpl(compositeDataSource);
        EmailNotificationGateway emailGateway = new EmailNotificationGatewayImpl(javaMailSender, mailFrom);
        this.orderCategoryService = new OrderCategoryService(this.productGateway);
        this.productAvailabilityService = new ProductAvailabilityService(this.productGateway);
        this.orderStatusUpdaterService = new OrderStatusUpdaterService(this.orderGateway, attendantGateway, emailGateway);
    }

    public static OrderController build(CompositeDataSource compositeDataSource, JavaMailSender javaMailSender, String mailFrom) {
        return new OrderController(compositeDataSource, javaMailSender, mailFrom);
    }

    public OrderResponseDTO create(CreateOrderInputDTO dto) {
        CreateOrderUseCase createOrderUseCase
                = new CreateOrderUseCase(orderGateway, customerGateway, orderCategoryService, productAvailabilityService);
        return OrderPresenter.toDTO(createOrderUseCase.execute(dto));
    }

    public OrderDetailsDTO findById(UUID id) {
        FindOrderByIdUseCase findOrderByIdUseCase = new FindOrderByIdUseCase(orderGateway);
        return OrderPresenter.toDetailsDTO(findOrderByIdUseCase.execute(id));
    }

    public OrderResponseDTO updateStatus(UpdateOrderStatusInputDTO dto) {
        UpdateOrderStatusUseCase updateOrderStatusUseCase = new UpdateOrderStatusUseCase(orderStatusUpdaterService);
        return OrderPresenter.toDTO(updateOrderStatusUseCase.execute(dto));
    }

    public OrderResponseDTO addItem(UUID orderId, UUID productId, int quantity) {
        AddItemInOrderUseCase addItemInOrderUseCase
                = new AddItemInOrderUseCase(orderGateway, productAvailabilityService);
        return OrderPresenter.toDTO(addItemInOrderUseCase.execute(orderId, productId, quantity));
    }

    public OrderResponseDTO removeItem(UUID orderId, UUID productId, int quantity) {
        RemoveItemFromOrderUseCase removeItemFromOrderUseCase
                = new RemoveItemFromOrderUseCase(orderGateway, productGateway);
        return OrderPresenter.toDTO(removeItemFromOrderUseCase.execute(orderId, productId, quantity));
    }

    public List<OrderStatusViewDTO> listTodayOrders() {
        ListTodayOrdersUseCase listTodayOrdersUseCase = new ListTodayOrdersUseCase(orderGateway);
        return OrderPresenter.toStatusViewDTOList(listTodayOrdersUseCase.execute());
    }

    public List<OrderSummaryDTO> listByPeriod(LocalDateTime initialDt, LocalDateTime finalDt) {
        ListOrdersByPeriodUseCase listOrdersByPeriodUseCase = new ListOrdersByPeriodUseCase(orderGateway);
        return OrderPresenter.toSummaryDTOList(listOrdersByPeriodUseCase.execute(initialDt, finalDt));
    }

}
