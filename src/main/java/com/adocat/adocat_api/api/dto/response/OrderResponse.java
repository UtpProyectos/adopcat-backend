package com.adocat.adocat_api.api.dto.response;

import com.adocat.adocat_api.domain.entity.Order;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Data
public class OrderResponse {
    private UUID orderId;
    private UUID userId;
    private String shippingAddress;
    private BigDecimal shippingCost;
    private BigDecimal totalAmount;
    private String paymentMethod;
    private String paymentStatus;
    private String status;
    private Instant createdAt;
    private Instant updatedAt;
    private String trackingCode;
    private List<OrderItemResponse> items;

    public OrderResponse(Order order) {
        this.orderId = order.getOrderId();
        this.userId = order.getUser().getUserId();
        this.shippingAddress = order.getShippingAddress();
        this.shippingCost = order.getShippingCost();
        this.totalAmount = order.getTotalAmount();
        this.paymentMethod = order.getPaymentMethod();
        this.paymentStatus = order.getPaymentStatus();
        this.status = order.getStatus();
        this.createdAt = order.getCreatedAt();
        this.updatedAt = order.getUpdatedAt();
        this.trackingCode = order.getTrackingCode();
        this.items = order.getItems().stream()
                .map(OrderItemResponse::new)
                .collect(Collectors.toList());
    }
}
