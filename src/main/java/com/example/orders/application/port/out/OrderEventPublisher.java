package com.example.orders.application.port.out;

import com.example.orders.domain.event.OrderEvent;

public interface OrderEventPublisher {

    void publish(OrderEvent event);
}
