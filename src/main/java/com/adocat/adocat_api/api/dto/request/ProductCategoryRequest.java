package com.adocat.adocat_api.api.dto.request;

import lombok.Data;
import java.util.UUID;

@Data
public class ProductCategoryRequest {
    private String name;
    private String description;
    private String imageUrl;
    private UUID parentCategoryId;
}