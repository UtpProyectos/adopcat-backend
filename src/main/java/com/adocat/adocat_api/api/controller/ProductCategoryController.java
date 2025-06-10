package com.adocat.adocat_api.api.controller;

import com.adocat.adocat_api.api.dto.request.ProductCategoryRequest;
import com.adocat.adocat_api.api.dto.response.ProductCategoryResponse;
import com.adocat.adocat_api.service.interfaces.ProductCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/product-categories")
@RequiredArgsConstructor
public class ProductCategoryController {

    private final ProductCategoryService categoryService;

    @PostMapping
    public ProductCategoryResponse create(@RequestBody ProductCategoryRequest request) {
        return categoryService.createCategory(request);
    }

    @GetMapping
    public List<ProductCategoryResponse> list() {
        return categoryService.getAllCategories();
    }

    @GetMapping("/{id}")
    public ProductCategoryResponse get(@PathVariable UUID id) {
        return categoryService.getCategoryById(id);
    }

    @PutMapping("/{id}")
    public ProductCategoryResponse update(@PathVariable UUID id, @RequestBody ProductCategoryRequest request) {
        return categoryService.updateCategory(id, request);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        categoryService.deleteCategory(id);
    }
}

