package com.adocat.adocat_api.service.impl;

import com.adocat.adocat_api.api.dto.ProductSupplierDto;
import com.adocat.adocat_api.domain.entity.ProductSupplier;
import com.adocat.adocat_api.domain.repository.ProductSupplierRepository;
import com.adocat.adocat_api.service.interfaces.ProductSupplierService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.sql.Timestamp;
import java.util.stream.Collectors;

@Service
public class ProductSupplierServiceImpl implements ProductSupplierService {

    @Autowired
    private ProductSupplierRepository repository;

    @Override
    public List<ProductSupplierDto> getAllSuppliers() {
        return repository.findAll().stream().map(ProductSupplierDto::fromEntity).collect(Collectors.toList());
    }

    @Override
    public ProductSupplierDto createSupplier(ProductSupplierDto dto) {
        ProductSupplier supplier = dto.toEntity();
        supplier.setSupplierId(UUID.randomUUID());
        supplier.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        return ProductSupplierDto.fromEntity(repository.save(supplier));
    }
}
