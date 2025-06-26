package com.adocat.adocat_api.api.controller;

import com.adocat.adocat_api.api.dto.request.ProductRequest;
import com.adocat.adocat_api.api.dto.request.VisibilityUpdateRequest;
import com.adocat.adocat_api.api.dto.response.ProductResponse;
import com.adocat.adocat_api.service.interfaces.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping
    public ProductResponse create(@RequestBody ProductRequest request) {
        return productService.createProduct(request);
    }

    @GetMapping
    public List<ProductResponse> list() {
        return productService.getAllProducts();
    }

    @GetMapping("/{id}")
    public ProductResponse get(@PathVariable UUID id) {
        return productService.getProductById(id);
    }

    @PutMapping("/{id}")
    public ProductResponse update(@PathVariable UUID id, @RequestBody ProductRequest request) {
        return productService.updateProduct(id, request);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        productService.deleteProduct(id);
    }

    // 🔁 NUEVO: Cambiar visibilidad (mostrar en catálogo o no)
    @PatchMapping("/{id}/visibility")
    public ProductResponse updateVisibility(@PathVariable UUID id, @RequestBody VisibilityUpdateRequest request) {
        return productService.updateVisibility(id, request.isVisible());
    }
}
