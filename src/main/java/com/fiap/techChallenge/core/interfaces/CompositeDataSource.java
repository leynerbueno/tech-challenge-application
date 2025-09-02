package com.fiap.techChallenge.core.interfaces;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.fiap.techChallenge.core.application.dto.order.OrderDTO;
import com.fiap.techChallenge.core.application.dto.order.projection.OrderWithItemsAndStatusProjection;
import com.fiap.techChallenge.core.application.dto.order.projection.OrderWithStatusAndWaitMinutesProjection;
import com.fiap.techChallenge.core.application.dto.order.projection.OrderWithStatusProjection;
import com.fiap.techChallenge.core.application.dto.payment.PaymentRequestDTO;
import com.fiap.techChallenge.core.application.dto.payment.PaymentResponseDTO;
import com.fiap.techChallenge.core.application.dto.product.ProductDTO;
import com.fiap.techChallenge.core.application.dto.user.AttendantDTO;
import com.fiap.techChallenge.core.application.dto.user.CustomerFullDTO;
import com.fiap.techChallenge.core.domain.entities.order.Order;
import com.fiap.techChallenge.core.domain.enums.Category;
import com.fiap.techChallenge.core.domain.enums.PaymentStatus;
import com.fiap.techChallenge.core.domain.enums.ProductStatus;

public interface CompositeDataSource {

    // Attendant
    AttendantDTO saveAttendant(AttendantDTO attendantDTO);
    AttendantDTO findFirstAttendantByCpf(String cpf);
    AttendantDTO findFirstAttendantById(UUID id);
    List<AttendantDTO> findAllAttendants();
    void deleteAttendant(UUID id);

    // Customer
    CustomerFullDTO saveCustomer(CustomerFullDTO attendantDTO);
    CustomerFullDTO findFirstCustomerByCpf(String cpf);
    CustomerFullDTO findFirstCustomerById(UUID id);
    List<CustomerFullDTO> findAllCustomerNotAnonym();
    void deleteCustomer(UUID id);

    // Payment
    PaymentResponseDTO processPayment(PaymentRequestDTO request, Order order);
    String findIdPayment(UUID paymentId);
    PaymentStatus checkStatus(UUID orderId);
    UUID findApprovedOrderByPaymentId(String paymentId);

    // Order
    OrderDTO saveOrder(OrderDTO order);
    OrderDTO findOrderById(UUID id);
    Optional<OrderWithItemsAndStatusProjection> findOrderWithDetailsById(UUID id);
    List<OrderWithStatusProjection> listOrderByPeriod(LocalDateTime initialDt, LocalDateTime finalDt);
    List<OrderWithStatusAndWaitMinutesProjection> listTodayOrders(List<String> statusList, int finalizedMinutes);
    OrderDTO findOrderIdByPaymentId(String paymentId);


    // Product
    ProductDTO saveProduct(ProductDTO product);
    ProductDTO findProductById(UUID id);
    ProductDTO findProductByName(String name);
    List<Category> listAvailableProductCategories();
    List<ProductDTO> listProducts();
    List<ProductDTO> listProductsByCategory(Category category);
    List<ProductDTO> listProductsByStatusAndCategory(ProductStatus status, Category category);
    List<ProductDTO> listProductsByStatus(ProductStatus status);
    void deleteProduct(UUID id);
    void deleteProductByCategory(Category category);
}
