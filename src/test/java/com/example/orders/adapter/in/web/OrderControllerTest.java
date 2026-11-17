package com.example.orders.adapter.in.web;

import com.example.orders.application.exception.OrderNotFoundException;
import com.example.orders.application.pagination.PageQuery;
import com.example.orders.application.pagination.PageResult;
import com.example.orders.application.port.in.CancelOrderUseCase;
import com.example.orders.application.port.in.CreateOrderCommand;
import com.example.orders.application.port.in.CreateOrderUseCase;
import com.example.orders.application.port.in.GetOrderUseCase;
import com.example.orders.application.port.in.ListOrdersUseCase;
import com.example.orders.domain.exception.OrderStateException;
import com.example.orders.domain.model.Money;
import com.example.orders.domain.model.Order;
import com.example.orders.domain.model.OrderId;
import com.example.orders.domain.model.OrderItem;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderController.class)
class OrderControllerTest {

    private static final Instant NOW = Instant.parse("2026-01-15T10:00:00Z");

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CreateOrderUseCase createOrder;

    @MockitoBean
    private GetOrderUseCase getOrder;

    @MockitoBean
    private ListOrdersUseCase listOrders;

    @MockitoBean
    private CancelOrderUseCase cancelOrder;

    @Test
    void createsOrder() throws Exception {
        Order order = sampleOrder();
        given(createOrder.create(any(CreateOrderCommand.class))).willReturn(order);

        mockMvc.perform(post("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "customerId": "customer-1",
                                  "items": [
                                    { "productId": "product-1", "quantity": 2, "unitPrice": 25.00 }
                                  ]
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/api/v1/orders/" + order.id()))
                .andExpect(jsonPath("$.id").value(order.id().toString()))
                .andExpect(jsonPath("$.status").value("CREATED"))
                .andExpect(jsonPath("$.total").value(50.0));
    }

    @Test
    void rejectsInvalidPayload() throws Exception {
        mockMvc.perform(post("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "customerId": "", "items": [] }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Invalid request"))
                .andExpect(jsonPath("$.errors.length()").value(2));

        verifyNoInteractions(createOrder);
    }

    @Test
    void returnsOrderById() throws Exception {
        Order order = sampleOrder();
        given(getOrder.getById(order.id())).willReturn(order);

        mockMvc.perform(get("/api/v1/orders/{id}", order.id().value()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerId").value("customer-1"))
                .andExpect(jsonPath("$.items[0].productId").value("product-1"));
    }

    @Test
    void returnsNotFoundForUnknownOrder() throws Exception {
        OrderId id = OrderId.newId();
        given(getOrder.getById(id)).willThrow(new OrderNotFoundException(id));

        mockMvc.perform(get("/api/v1/orders/{id}", id.value()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Order not found"));
    }

    @Test
    void returnsBadRequestForMalformedId() throws Exception {
        mockMvc.perform(get("/api/v1/orders/{id}", "not-a-uuid"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void listsOrders() throws Exception {
        given(listOrders.list(new PageQuery(0, 20)))
                .willReturn(new PageResult<>(List.of(sampleOrder()), 0, 20, 1));

        mockMvc.perform(get("/api/v1/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.totalPages").value(1));
    }

    @Test
    void returnsUnprocessableEntityWhenBusinessRuleFails() throws Exception {
        OrderId id = OrderId.newId();
        given(cancelOrder.cancel(id)).willThrow(new OrderStateException("order " + id + " is already cancelled"));

        mockMvc.perform(post("/api/v1/orders/{id}/cancel", id.value()))
                .andExpect(status().is(422))
                .andExpect(jsonPath("$.title").value("Business rule violation"));
    }

    private static Order sampleOrder() {
        return Order.create("customer-1", List.of(new OrderItem("product-1", 2, Money.of("25.00"))), NOW);
    }
}
