package com.example.orders;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class OrderApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void createsFetchesAndCancelsAnOrder() throws Exception {
        String location = mockMvc.perform(post("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "customerId": "customer-42",
                                  "items": [
                                    { "productId": "book-1", "quantity": 1, "unitPrice": 59.90 },
                                    { "productId": "pen-7", "quantity": 3, "unitPrice": 4.50 }
                                  ]
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.total").value(73.4))
                .andReturn()
                .getResponse()
                .getHeader("Location");

        mockMvc.perform(get(location))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CREATED"))
                .andExpect(jsonPath("$.items.length()").value(2));

        mockMvc.perform(post(location + "/cancel"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELLED"));

        mockMvc.perform(post(location + "/cancel"))
                .andExpect(status().is(422));
    }

    @Test
    void returnsNotFoundForUnknownOrder() throws Exception {
        mockMvc.perform(get("/api/v1/orders/{id}", "00000000-0000-0000-0000-000000000000"))
                .andExpect(status().isNotFound());
    }
}
