package com.adocat.adocat_api.api.controller;

import com.adocat.adocat_api.api.dto.ProductDto;
import com.adocat.adocat_api.service.interfaces.ProductService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductService service;

    @GetMapping
    public List<ProductDto> getAll() {
        return service.getAllProducts();
    }

    @PostMapping
    public ProductDto create(@RequestBody ProductDto dto) {
        return service.createProduct(dto);
    }
}
