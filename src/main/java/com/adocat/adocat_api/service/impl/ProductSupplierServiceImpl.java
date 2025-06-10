package com.adocat.adocat_api.service.impl;

import com.adocat.adocat_api.api.dto.request.ProductSupplierRequest;
import com.adocat.adocat_api.api.dto.response.ProductSupplierResponse;
import com.adocat.adocat_api.domain.entity.ProductSupplier;
import com.adocat.adocat_api.domain.repository.ProductSupplierRepository;
import com.adocat.adocat_api.service.interfaces.ProductSupplierService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductSupplierServiceImpl implements ProductSupplierService {

    private final ProductSupplierRepository supplierRepository;

    @Override
    public ProductSupplierResponse createSupplier(ProductSupplierRequest request) {
        ProductSupplier supplier = ProductSupplier.builder()
                .name(request.getName())
                .contactInfo(request.getContactInfo())
                .build();

        return new ProductSupplierResponse(supplierRepository.save(supplier));
    }

    @Override
    public List<ProductSupplierResponse> getAllSuppliers() {
        return supplierRepository.findAll().stream()
                .map(ProductSupplierResponse::new)
                .collect(Collectors.toList());
    }

    @Override
    public ProductSupplierResponse getSupplierById(UUID id) {
        return supplierRepository.findById(id)
                .map(ProductSupplierResponse::new)
                .orElseThrow(() -> new RuntimeException("Proveedor no encontrado"));
    }

    @Override
    public ProductSupplierResponse updateSupplier(UUID id, ProductSupplierRequest request) {
        ProductSupplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Proveedor no encontrado"));

        supplier.setName(request.getName());
        supplier.setContactInfo(request.getContactInfo());

        return new ProductSupplierResponse(supplierRepository.save(supplier));
    }

    @Override
    public void deleteSupplier(UUID id) {
        supplierRepository.deleteById(id);
    }
}
