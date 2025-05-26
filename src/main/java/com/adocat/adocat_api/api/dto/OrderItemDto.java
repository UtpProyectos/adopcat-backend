package com.adocat.adocat_api.api.dto;

import com.adocat.adocat_api.domain.entity.OrderItem;

import java.math.BigDecimal;
import java.util.UUID;

public class OrderItemDto {
    public UUID itemId;
    public UUID orderId;
    public UUID productId;
    public Integer quantity;
    public BigDecimal unitPrice;
    public BigDecimal discountApplied;
    public BigDecimal subtotal;

    public static OrderItemDto fromEntity(OrderItem item) {
        OrderItemDto dto = new OrderItemDto();
        dto.itemId = item.getItemId();
        dto.orderId = item.getOrderId();
        dto.productId = item.getProductId();
        dto.quantity = item.getQuantity();
        dto.unitPrice = item.getUnitPrice();
        dto.discountApplied = item.getDiscountApplied();
        dto.subtotal = item.getSubtotal();
        return dto;
    }

    public OrderItem toEntity() {
        OrderItem item = new OrderItem();
        item.setItemId(itemId);
        item.setOrderId(orderId);
        item.setProductId(productId);
        item.setQuantity(quantity);
        item.setUnitPrice(unitPrice);
        item.setDiscountApplied(discountApplied);
        item.setSubtotal(subtotal);
        return item;
    }
}
