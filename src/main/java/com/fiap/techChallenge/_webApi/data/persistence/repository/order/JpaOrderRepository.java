package com.fiap.techChallenge._webApi.data.persistence.repository.order;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.fiap.techChallenge._webApi.data.persistence.entity.order.OrderEntity;
import com.fiap.techChallenge.core.application.dto.order.projection.OrderWithItemsAndStatusProjection;
import com.fiap.techChallenge.core.application.dto.order.projection.OrderWithStatusAndWaitMinutesProjection;
import com.fiap.techChallenge.core.application.dto.order.projection.OrderWithStatusProjection;

@Repository
public interface JpaOrderRepository extends JpaRepository<OrderEntity, UUID> {

    OrderEntity findFirstById(UUID id);

    List<OrderEntity> findAllByCustomerId(UUID id);

    @Query(value = """
    SELECT 
        BIN_TO_UUID(h.order_id) AS orderId,
        h.status AS status,
        h.date AS statusDt,
        BIN_TO_UUID(o.customer_id) AS customerId,
        c.name AS customerName,
        BIN_TO_UUID(h.attendant_id) AS attendantId,
        a.name AS attendantName,
        o.date AS orderDt,
        TIMESTAMPDIFF(MINUTE, o.date, NOW()) AS waitTimeMinutes
    FROM order_status_history h

    INNER JOIN (
        SELECT order_id, MAX(date) AS latest_date
        FROM order_status_history
        GROUP BY order_id
    ) latest ON h.order_id = latest.order_id AND h.date = latest.latest_date

    INNER JOIN `order` o ON o.id = h.order_id

    INNER JOIN user c ON c.id = o.customer_id

    LEFT JOIN user a ON a.id = h.attendant_id

    WHERE h.status IN (:statusList)
        AND DATE(h.date) = CURDATE()

    ORDER BY 
        FIELD(h.status, 'PRONTO', 'EM_PREPARACAO', 'RECEBIDO'),
        h.date ASC
    """, nativeQuery = true)
    List<OrderWithStatusAndWaitMinutesProjection> findTodayOrders(
            @Param("statusList") List<String> statusList,
            @Param("finalizedMinutes") int finalizedMinutes
    );

    @Query(value = """
    SELECT
        BIN_TO_UUID(o.id) AS orderId,
        h.status AS status,
        h.date AS statusDt,
        BIN_TO_UUID(o.customer_id) AS customerId,
        c.name AS customerName,
        BIN_TO_UUID(h.attendant_id) AS attendantId,
        a.name AS attendantName,
        o.price AS price,
        o.date AS orderDt,
        o.payment_id as paymentId,
        JSON_ARRAYAGG(
            JSON_OBJECT(
                'productId', BIN_TO_UUID(i.product_id),
                'productName', i.product_name,
                'category', i.category,
                'quantity', i.quantity,
                'unitPrice', i.unit_price
            )
        ) AS itemsJson
    FROM order_status_history h
    
    INNER JOIN (
        SELECT order_id, MAX(date) AS latest_date
        FROM order_status_history
        GROUP BY order_id
    ) latest ON h.order_id = latest.order_id AND h.date = latest.latest_date
    
    INNER JOIN `order` o ON o.id = h.order_id
    INNER JOIN user c ON c.id = o.customer_id
    LEFT JOIN user a ON a.id = h.attendant_id
    
    LEFT JOIN order_items i ON o.id = i.order_id
    
    WHERE BIN_TO_UUID(o.id) = :id
    
    GROUP BY
        o.id, h.status, h.date, c.id, c.name, h.attendant_id, a.name
        """, nativeQuery = true)
    Optional<OrderWithItemsAndStatusProjection> findWithDetailsById(@Param("id") String id);

    @Query(value = """
        SELECT 
            BIN_TO_UUID(o.id) AS orderId,
            h.status AS status,
            h.date AS statusDt,
            BIN_TO_UUID(o.customer_id) AS customerId,
            c.name AS customerName,
            BIN_TO_UUID(h.attendant_id) AS attendantId,
            a.name AS attendantName,
            o.price AS price,
            o.date AS orderDt
        FROM `order` o

        INNER JOIN order_status_history h ON h.order_id = o.id

        INNER JOIN (
            SELECT order_id, MAX(date) AS latest_date
            FROM order_status_history
            GROUP BY order_id
        ) latest ON h.order_id = latest.order_id AND h.date = latest.latest_date

        INNER JOIN user c ON c.id = o.customer_id

        LEFT JOIN user a ON a.id = h.attendant_id

        WHERE h.date BETWEEN :startDt AND :endDt

        ORDER BY 
            FIELD(h.status, 'RECEBIDO', 'EM_PREPARACAO', 'PRONTO', 'FINALIZADO', 'CANCELADO'),
            o.date
    """, nativeQuery = true)
    List<OrderWithStatusProjection> findAllByOrderDt(@Param("startDt") LocalDateTime startDt, @Param("endDt") LocalDateTime endDt);

    OrderEntity findByPaymentId(String paymentId);
}
