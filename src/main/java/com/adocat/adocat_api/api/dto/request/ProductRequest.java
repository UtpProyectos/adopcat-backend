package com.adocat.adocat_api.api.dto.request;

import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class ProductRequest {
    private String name;
    private String description;
    private BigDecimal price;
    private BigDecimal discountPct;
    private String imageUrl;
    private UUID categoryId;
    private UUID supplierId;
}