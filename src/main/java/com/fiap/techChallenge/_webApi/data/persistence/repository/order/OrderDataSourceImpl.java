package com.fiap.techChallenge._webApi.data.persistence.repository.order;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.fiap.techChallenge._webApi.mappers.OrderMapper;
import com.fiap.techChallenge._webApi.data.persistence.entity.order.OrderEntity;
import com.fiap.techChallenge.core.application.dto.order.OrderDTO;
import com.fiap.techChallenge.core.application.dto.order.projection.OrderWithItemsAndStatusProjection;
import com.fiap.techChallenge.core.application.dto.order.projection.OrderWithStatusAndWaitMinutesProjection;
import com.fiap.techChallenge.core.application.dto.order.projection.OrderWithStatusProjection;
import com.fiap.techChallenge.core.interfaces.OrderDataSource;

@Component
public class OrderDataSourceImpl implements OrderDataSource {

    private final JpaOrderRepository repository;
    
    public OrderDataSourceImpl(JpaOrderRepository repository) {
        this.repository = repository;
    }

    @Override
    public OrderDTO save(OrderDTO order) {
        OrderEntity entity = OrderMapper.toEntity(order);
        entity = repository.save(entity);

        return OrderMapper.toDTO(entity);
    }

    @Override
    public OrderDTO findById(UUID id) {
        return OrderMapper.toDTO(repository.findFirstById(id));
    }

    @Override
    public Optional<OrderWithItemsAndStatusProjection> findWithDetailsById(UUID id) {
        return repository.findWithDetailsById(id.toString());
    }

    @Override
    public List<OrderWithStatusProjection> listByPeriod(LocalDateTime initialDt, LocalDateTime finalDt) {
        return repository.findAllByOrderDt(initialDt, finalDt);
    }

    @Override
    public List<OrderWithStatusAndWaitMinutesProjection> listTodayOrders(List<String> statusList,
            int finalizedMinutes) {
        return repository.findTodayOrders(statusList, finalizedMinutes);
    }

    @Override
    public OrderDTO findByPaymentId(String paymentId) {
        return OrderMapper.toDTO(repository.findByPaymentId(paymentId));
    }
}
