package com.adocat.adocat_api.api.dto.request;

import lombok.Data;

import java.util.List;

@Data
public class OrderRequest {
    private String shippingAddress;
    private String paymentMethod;
    private List<OrderItemRequest> items;
}
