package com.example.orders.application.service;

import com.example.orders.application.port.out.OrderEventPublisher;
import com.example.orders.domain.event.OrderEvent;

import java.util.ArrayList;
import java.util.List;

class RecordingOrderEventPublisher implements OrderEventPublisher {

    private final List<OrderEvent> events = new ArrayList<>();

    @Override
    public void publish(OrderEvent event) {
        events.add(event);
    }

    List<OrderEvent> events() {
        return List.copyOf(events);
    }
}
