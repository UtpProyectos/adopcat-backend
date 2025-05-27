package com.adocat.adocat_api.service.impl;

import com.adocat.adocat_api.api.dto.ProductCategoryDto;
import com.adocat.adocat_api.domain.entity.ProductCategory;
import com.adocat.adocat_api.domain.repository.ProductCategoryRepository;
import com.adocat.adocat_api.service.interfaces.ProductCategoryService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ProductCategoryServiceImpl implements ProductCategoryService {

    @Autowired
    private ProductCategoryRepository repository;

    @Override
    public List<ProductCategoryDto> getAllCategories() {
        return repository.findAll().stream().map(ProductCategoryDto::fromEntity).collect(Collectors.toList());
    }

    @Override
    public ProductCategoryDto createCategory(ProductCategoryDto dto) {
        ProductCategory entity = dto.toEntity();
        entity.setCategoryId(UUID.randomUUID());
        return ProductCategoryDto.fromEntity(repository.save(entity));
    }
}
