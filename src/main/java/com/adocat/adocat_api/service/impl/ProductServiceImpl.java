package com.adocat.adocat_api.service.impl;

import com.adocat.adocat_api.api.dto.ProductDto;
import com.adocat.adocat_api.domain.entity.Product;
import com.adocat.adocat_api.domain.repository.ProductRepository;
import com.adocat.adocat_api.service.interfaces.ProductService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository repository;

    @Override
    public List<ProductDto> getAllProducts() {
        return repository.findAll().stream().map(ProductDto::fromEntity).collect(Collectors.toList());
    }

    @Override
    public ProductDto createProduct(ProductDto dto) {
        Product product = dto.toEntity();
        product.setProductId(UUID.randomUUID());
        return ProductDto.fromEntity(repository.save(product));
    }
}
