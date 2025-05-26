package com.adocat.adocat_api.api.dto;

import com.adocat.adocat_api.domain.entity.ProductCategory;
import java.util.UUID;

public class ProductCategoryDto {
    public UUID categoryId;
    public String name;
    public String description;
    public UUID parentCategoryId;
    public String imageUrl;

    public static ProductCategoryDto fromEntity(ProductCategory pc) {
        ProductCategoryDto dto = new ProductCategoryDto();
        dto.categoryId = pc.getCategoryId();
        dto.name = pc.getName();
        dto.description = pc.getDescription();
        dto.parentCategoryId = pc.getParentCategoryId();
        dto.imageUrl = pc.getImageUrl();
        return dto;
    }

    public ProductCategory toEntity() {
        ProductCategory pc = new ProductCategory();
        pc.setCategoryId(categoryId);
        pc.setName(name);
        pc.setDescription(description);
        pc.setParentCategoryId(parentCategoryId);
        pc.setImageUrl(imageUrl);
        return pc;
    }
}
