package com.adocat.adocat_api.api.dto.response;

import com.adocat.adocat_api.domain.entity.ProductCategory;
import lombok.Data;

import java.util.UUID;

@Data
public class ProductCategoryResponse {
    private UUID categoryId;
    private String name;
    private String description;
    private String imageUrl;
    private UUID parentCategoryId;

    public ProductCategoryResponse(ProductCategory category) {
        this.categoryId = category.getCategoryId();
        this.name = category.getName();
        this.description = category.getDescription();
        this.imageUrl = category.getImageUrl();
        this.parentCategoryId = category.getParentCategory() != null ? category.getParentCategory().getCategoryId() : null;
    }
}