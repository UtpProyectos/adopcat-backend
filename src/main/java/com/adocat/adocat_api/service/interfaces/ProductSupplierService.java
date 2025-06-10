package com.adocat.adocat_api.service.interfaces;

import com.adocat.adocat_api.api.dto.request.ProductSupplierRequest;
import com.adocat.adocat_api.api.dto.response.ProductSupplierResponse;

import java.util.List;
import java.util.UUID;

public interface ProductSupplierService {
    ProductSupplierResponse createSupplier(ProductSupplierRequest request);
    List<ProductSupplierResponse> getAllSuppliers();
    ProductSupplierResponse getSupplierById(UUID id);
    ProductSupplierResponse updateSupplier(UUID id, ProductSupplierRequest request);
    void deleteSupplier(UUID id);
}
