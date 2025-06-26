package com.adocat.adocat_api.api.dto.request;

import lombok.Data;

import java.util.UUID;

@Data
public class OrderItemRequest {
    private String productId;
    private Integer quantity;
    private String name;

}
