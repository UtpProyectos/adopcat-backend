package com.adocat.adocat_api.api.dto.response;

import com.adocat.adocat_api.domain.entity.Product;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class ProductResponse {
    private UUID productId;
    private String name;
    private String description;
    private BigDecimal price;
    private BigDecimal discountPct;
    private String imageUrl;
    private Boolean isActive;
    private UUID categoryId;
    private String categoryName;
    private UUID supplierId;
    private String supplierName;

    public ProductResponse(Product product) {
        this.productId = product.getProductId();
        this.name = product.getName();
        this.description = product.getDescription();
        this.price = product.getPrice();
        this.discountPct = product.getDiscountPct();
        this.imageUrl = product.getImageUrl();
        this.isActive = product.getIsActive();

        // Relaciones
        this.categoryId = product.getCategory() != null ? product.getCategory().getCategoryId() : null;
        this.categoryName = product.getCategory() != null ? product.getCategory().getName() : null;

        this.supplierId = product.getSupplier() != null ? product.getSupplier().getSupplierId() : null;
        this.supplierName = product.getSupplier() != null ? product.getSupplier().getName() : null;
    }
}
