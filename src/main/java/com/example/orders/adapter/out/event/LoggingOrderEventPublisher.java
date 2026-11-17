package com.example.orders.adapter.out.event;

import com.example.orders.application.port.out.OrderEventPublisher;
import com.example.orders.domain.event.OrderCancelled;
import com.example.orders.domain.event.OrderCreated;
import com.example.orders.domain.event.OrderEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
class LoggingOrderEventPublisher implements OrderEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(LoggingOrderEventPublisher.class);

    @Override
    public void publish(OrderEvent event) {
        switch (event) {
            case OrderCreated created -> log.info("Order created: id={}, customer={}, total={}",
                    created.orderId(), created.customerId(), created.total().amount());
            case OrderCancelled cancelled -> log.info("Order cancelled: id={}", cancelled.orderId());
        }
    }
}
