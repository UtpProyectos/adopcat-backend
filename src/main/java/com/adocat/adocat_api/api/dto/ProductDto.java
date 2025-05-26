package com.adocat.adocat_api.api.dto;

import com.adocat.adocat_api.domain.entity.Product;
import java.math.BigDecimal;
import java.util.UUID;

public class ProductDto {
    public UUID productId;
    public String name;
    public String description;
    public BigDecimal price;
    public BigDecimal discountPct;
    public Boolean isActive;
    public String imageUrl;

    public static ProductDto fromEntity(Product p) {
        ProductDto dto = new ProductDto();
        dto.productId = p.getProductId();
        dto.name = p.getName();
        dto.description = p.getDescription();
        dto.price = p.getPrice();
        dto.discountPct = p.getDiscountPct();
        dto.imageUrl = p.getImageUrl();
        dto.isActive = p.getIsActive();
        return dto;
    }

    public Product toEntity() {
        Product p = new Product();
        p.setProductId(productId);
        p.setName(name);
        p.setDescription(description);
        p.setPrice(price);
        p.setDiscountPct(discountPct);
        p.setImageUrl(imageUrl);
        p.setIsActive(isActive);
        return p;
    }
}
