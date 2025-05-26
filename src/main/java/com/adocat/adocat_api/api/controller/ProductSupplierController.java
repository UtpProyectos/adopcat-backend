package com.adocat.adocat_api.api.controller;

import com.adocat.adocat_api.api.dto.ProductSupplierDto;
import com.adocat.adocat_api.service.interfaces.ProductSupplierService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/suppliers")
public class ProductSupplierController {

    @Autowired
    private ProductSupplierService service;

    @GetMapping
    public List<ProductSupplierDto> getAll() {
        return service.getAllSuppliers();
    }

    @PostMapping
    public ProductSupplierDto create(@RequestBody ProductSupplierDto dto) {
        return service.createSupplier(dto);
    }
}
