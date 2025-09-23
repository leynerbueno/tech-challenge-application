package com.fiap.techChallenge._webApi.controller.order;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fiap.techChallenge._webApi.dto.order.CreateOrderDTO;
import com.fiap.techChallenge._webApi.dto.order.UpdateOrderStatusDTO;
import com.fiap.techChallenge._webApi.mappers.OrderMapper;
import com.fiap.techChallenge.core.application.dto.order.OrderDetailsDTO;
import com.fiap.techChallenge.core.application.dto.order.OrderResponseDTO;
import com.fiap.techChallenge.core.application.dto.order.OrderStatusViewDTO;
import com.fiap.techChallenge.core.application.dto.order.OrderSummaryDTO;
import com.fiap.techChallenge.core.application.dto.order.UpdateOrderStatusInputDTO;
import com.fiap.techChallenge.core.controller.order.OrderController;
import com.fiap.techChallenge.core.interfaces.CompositeDataSource;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;

@RestController
@RequestMapping("/api/order")
@Tag(name = "Order", description = "APIs relacionadas aos Pedidos feitos pelos clientes")
public class OrderWebController {

    private final OrderController orderController;

    public OrderWebController(CompositeDataSource compositeDataSource, JavaMailSender javaMailSender, @Value("${app.mail.from}") String mailFrom) {
        this.orderController = OrderController.build(compositeDataSource, javaMailSender, mailFrom);
    }

    @Transactional
    @PostMapping("/create")
    @Operation(summary = "Create", description = "Cria um Pedido que será realizado pelo cliente")
    public ResponseEntity<OrderResponseDTO> save(@RequestBody @Valid CreateOrderDTO order) {
        return ResponseEntity.ok(orderController.create(OrderMapper.toCreateOrderInputDTO(order)));
    }

    @Transactional
    @PostMapping("/add-item/{orderId}/{productId}/{quantity}")
    @Operation(summary = "Add Item",
            description = "Adiciona um Produto ao Pedido")
    public ResponseEntity<OrderResponseDTO> addItem(@PathVariable UUID orderId,
            @PathVariable UUID productId,
            @PathVariable @Min(value = 1, message = "A quantidade deve ser maior que zero") int quantity) {
        return ResponseEntity.ok(orderController.addItem(orderId, productId, quantity));
    }

    @Transactional
    @PostMapping("/remove-item/{orderId}/{productId}/{quantity}")
    @Operation(summary = "Remove Item",
            description = "Remove um Produto do Pedido")
    public ResponseEntity<OrderResponseDTO> removeItem(@PathVariable UUID orderId, @PathVariable UUID productId, @PathVariable int quantity) {
        return ResponseEntity.ok(orderController.removeItem(orderId, productId, quantity));
    }

    @GetMapping("/find-by-id/{id}")
    @Operation(summary = "Find By ID",
            description = "Encontra um pedido pelo ID")
    public ResponseEntity<OrderDetailsDTO> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(orderController.findById(id));
    }

    @Transactional
    @PostMapping("/update-status")
    @Operation(summary = "Update Status",
            description = "Atualiza o status de um pedido")
    public ResponseEntity<OrderResponseDTO> updateStatus(@RequestBody @Valid UpdateOrderStatusDTO dto) {
        return ResponseEntity.ok(orderController.updateStatus(
                UpdateOrderStatusInputDTO.builder()
                        .orderId(dto.orderId())
                        .status(dto.status())
                        .attendantId(dto.attendantId())
                        .build()
        ));
    }

    @GetMapping("/list-today-orders")
    @Operation(summary = "List Today Order",
            description = "Lista os pedidos em Andamento")
    public ResponseEntity<List<OrderStatusViewDTO>> listTodayOrders() {
        return ResponseEntity.ok(orderController.listTodayOrders());
    }

    @GetMapping("/list-by-period/{initialDt}/{finalDt}")
    @Operation(summary = "List By Period",
            description = "Encontra um pedido pelo periodo informado")
    public ResponseEntity<List<OrderSummaryDTO>> listByPeriod(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime initialDt,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime finalDt) {
        return ResponseEntity.ok(orderController.listByPeriod(initialDt, finalDt));
    }

}
