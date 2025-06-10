package com.adocat.adocat_api.api.dto.response;

import com.adocat.adocat_api.domain.entity.OrderItem;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class OrderItemResponse {
    private UUID itemId;
    private UUID productId;
    private String productName;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal discountApplied;
    private BigDecimal subtotal;

    public OrderItemResponse(OrderItem item) {
        this.itemId = item.getItemId();
        this.productId = item.getProduct().getProductId();
        this.productName = item.getProduct().getName();
        this.quantity = item.getQuantity();
        this.unitPrice = item.getUnitPrice();
        this.discountApplied = item.getDiscountApplied();
        this.subtotal = item.getSubtotal();
    }
}
