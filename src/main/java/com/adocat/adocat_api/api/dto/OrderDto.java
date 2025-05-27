package com.adocat.adocat_api.api.dto;

import com.adocat.adocat_api.domain.entity.Order;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.UUID;

public class OrderDto {

    public UUID orderId;
    public UUID userId;
    public String status;
    public String shippingAddress;
    public BigDecimal shippingCost;
    public BigDecimal totalAmount;
    public String paymentMethod;
    public String paymentStatus;
    public String trackingCode;
    public Timestamp createdAt;
    public Timestamp updatedAt;

    public static OrderDto fromEntity(Order order) {
        OrderDto dto = new OrderDto();
        dto.orderId = order.getOrderId();
        dto.userId = order.getUserId();
        dto.status = order.getStatus();
        dto.shippingAddress = order.getShippingAddress();
        dto.shippingCost = order.getShippingCost();
        dto.totalAmount = order.getTotalAmount();
        dto.paymentMethod = order.getPaymentMethod();
        dto.paymentStatus = order.getPaymentStatus();
        dto.trackingCode = order.getTrackingCode();
        dto.createdAt = order.getCreatedAt();
        dto.updatedAt = order.getUpdatedAt();
        return dto;
    }

    public Order toEntity() {
        Order order = new Order();
        order.setOrderId(orderId);
        order.setUserId(userId);
        order.setStatus(status);
        order.setShippingAddress(shippingAddress);
        order.setShippingCost(shippingCost);
        order.setTotalAmount(totalAmount);
        order.setPaymentMethod(paymentMethod);
        order.setPaymentStatus(paymentStatus);
        order.setTrackingCode(trackingCode);
        order.setCreatedAt(createdAt);
        order.setUpdatedAt(updatedAt);
        return order;
    }
}
