package com.adocat.adocat_api.service.interfaces;

import com.adocat.adocat_api.api.dto.request.OrderRequest;
import com.adocat.adocat_api.api.dto.response.OrderResponse;

import java.util.List;
import java.util.UUID;

public interface OrderService {
    OrderResponse createOrder(UUID userId, OrderRequest request);
    List<OrderResponse> getOrdersByUser(UUID userId);
    OrderResponse getOrderById(UUID orderId);
}
