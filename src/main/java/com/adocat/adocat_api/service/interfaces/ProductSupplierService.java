package com.adocat.adocat_api.service.interfaces;

import com.adocat.adocat_api.api.dto.ProductSupplierDto;
import java.util.List;

public interface ProductSupplierService {
    List<ProductSupplierDto> getAllSuppliers();
    ProductSupplierDto createSupplier(ProductSupplierDto dto);
}
