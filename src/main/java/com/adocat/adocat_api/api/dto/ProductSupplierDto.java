package com.adocat.adocat_api.api.dto;

import com.adocat.adocat_api.domain.entity.ProductSupplier;

import java.sql.Timestamp;
import java.util.UUID;

public class ProductSupplierDto {
    public UUID supplierId;
    public String name;
    public String contactInfo;
    public Timestamp createdAt;

    public static ProductSupplierDto fromEntity(ProductSupplier supplier) {
        ProductSupplierDto dto = new ProductSupplierDto();
        dto.supplierId = supplier.getSupplierId();
        dto.name = supplier.getName();
        dto.contactInfo = supplier.getContactInfo();
        dto.createdAt = supplier.getCreatedAt();
        return dto;
    }

    public ProductSupplier toEntity() {
        ProductSupplier s = new ProductSupplier();
        s.setSupplierId(supplierId);
        s.setName(name);
        s.setContactInfo(contactInfo);
        s.setCreatedAt(createdAt);
        return s;
    }
}
