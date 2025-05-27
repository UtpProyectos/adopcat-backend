package com.adocat.adocat_api.service.interfaces;

import com.adocat.adocat_api.api.dto.OrderDto;
import java.util.List;

public interface OrderService {
    List<OrderDto> getAllOrders();
    OrderDto createOrder(OrderDto dto);
}
