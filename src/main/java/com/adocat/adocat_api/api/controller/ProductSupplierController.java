package com.adocat.adocat_api.api.controller;

import com.adocat.adocat_api.api.dto.request.ProductSupplierRequest;
import com.adocat.adocat_api.api.dto.response.ProductSupplierResponse;
import com.adocat.adocat_api.service.interfaces.ProductSupplierService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/product-suppliers")
@RequiredArgsConstructor
public class ProductSupplierController {

    private final ProductSupplierService supplierService;

    @PostMapping
    public ProductSupplierResponse create(@RequestBody ProductSupplierRequest request) {
        return supplierService.createSupplier(request);
    }

    @GetMapping
    public List<ProductSupplierResponse> list() {
        return supplierService.getAllSuppliers();
    }

    @GetMapping("/{id}")
    public ProductSupplierResponse get(@PathVariable UUID id) {
        return supplierService.getSupplierById(id);
    }

    @PutMapping("/{id}")
    public ProductSupplierResponse update(@PathVariable UUID id, @RequestBody ProductSupplierRequest request) {
        return supplierService.updateSupplier(id, request);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        supplierService.deleteSupplier(id);
    }
}
