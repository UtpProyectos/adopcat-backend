package com.adocat.adocat_api.api.controller;

import com.adocat.adocat_api.api.dto.ProductCategoryDto;
import com.adocat.adocat_api.service.interfaces.ProductCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class ProductCategoryController {

    @Autowired
    private ProductCategoryService service;

    @GetMapping
    public List<ProductCategoryDto> getAll() {
        return service.getAllCategories();
    }

    @PostMapping
    public ProductCategoryDto create(@RequestBody ProductCategoryDto dto) {
        return service.createCategory(dto);
    }
}
