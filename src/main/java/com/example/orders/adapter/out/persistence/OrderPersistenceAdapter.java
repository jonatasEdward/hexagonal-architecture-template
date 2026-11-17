package com.example.orders.adapter.out.persistence;

import com.example.orders.application.exception.ConcurrentOrderUpdateException;
import com.example.orders.application.pagination.PageQuery;
import com.example.orders.application.pagination.PageResult;
import com.example.orders.application.port.out.OrderRepository;
import com.example.orders.domain.model.Order;
import com.example.orders.domain.model.OrderId;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component
class OrderPersistenceAdapter implements OrderRepository {

    private static final Sort DEFAULT_SORT = Sort.by(Sort.Direction.DESC, "createdAt");

    private final OrderJpaRepository jpaRepository;

    OrderPersistenceAdapter(OrderJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    @Transactional
    public Order save(Order order) {
        OrderJpaEntity entity = jpaRepository.findById(order.id().value())
                .map(existing -> {
                    if (existing.getVersion() != order.version()) {
                        throw new ConcurrentOrderUpdateException(order.id());
                    }
                    existing.updateState(order.status(), OrderPersistenceMapper.toEmbeddables(order.items()));
                    return existing;
                })
                .orElseGet(() -> OrderPersistenceMapper.toEntity(order));

        try {
            return OrderPersistenceMapper.toDomain(jpaRepository.saveAndFlush(entity));
        } catch (OptimisticLockingFailureException ex) {
            throw new ConcurrentOrderUpdateException(order.id());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Order> findById(OrderId id) {
        return jpaRepository.findById(id.value()).map(OrderPersistenceMapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResult<Order> findAll(PageQuery query) {
        Page<OrderJpaEntity> page = jpaRepository.findAll(PageRequest.of(query.page(), query.size(), DEFAULT_SORT));

        return new PageResult<>(
                page.getContent().stream().map(OrderPersistenceMapper::toDomain).toList(),
                query.page(),
                query.size(),
                page.getTotalElements());
    }
}
