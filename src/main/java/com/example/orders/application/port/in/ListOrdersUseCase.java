package com.example.orders.application.port.in;

import com.example.orders.application.pagination.PageQuery;
import com.example.orders.application.pagination.PageResult;
import com.example.orders.domain.model.Order;

public interface ListOrdersUseCase {

    PageResult<Order> list(PageQuery query);
}
