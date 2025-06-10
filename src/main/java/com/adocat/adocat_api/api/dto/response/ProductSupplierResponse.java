package com.adocat.adocat_api.api.dto.response;

import com.adocat.adocat_api.domain.entity.ProductSupplier;
import lombok.Data;

import java.util.UUID;

@Data
public class ProductSupplierResponse {
    private UUID supplierId;
    private String name;
    private String contactInfo;

    public ProductSupplierResponse(ProductSupplier supplier) {
        this.supplierId = supplier.getSupplierId();
        this.name = supplier.getName();
        this.contactInfo = supplier.getContactInfo();
    }
}