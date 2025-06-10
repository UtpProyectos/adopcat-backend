package com.adocat.adocat_api.service.impl;

import com.adocat.adocat_api.api.dto.request.ProductCategoryRequest;
import com.adocat.adocat_api.api.dto.response.ProductCategoryResponse;
import com.adocat.adocat_api.domain.entity.ProductCategory;
import com.adocat.adocat_api.domain.repository.ProductCategoryRepository;
import com.adocat.adocat_api.service.interfaces.ProductCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductCategoryServiceImpl implements ProductCategoryService {

    private final ProductCategoryRepository categoryRepository;

    @Override
    public ProductCategoryResponse createCategory(ProductCategoryRequest request) {
        ProductCategory parent = null;
        if (request.getParentCategoryId() != null) {
            parent = categoryRepository.findById(request.getParentCategoryId())
                    .orElseThrow(() -> new RuntimeException("Categoría padre no encontrada"));
        }

        ProductCategory category = ProductCategory.builder()
                .name(request.getName())
                .description(request.getDescription())
                .imageUrl(request.getImageUrl())
                .parentCategory(parent)
                .build();

        return new ProductCategoryResponse(categoryRepository.save(category));
    }

    @Override
    public List<ProductCategoryResponse> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(ProductCategoryResponse::new)
                .collect(Collectors.toList());
    }

    @Override
    public ProductCategoryResponse getCategoryById(UUID id) {
        return categoryRepository.findById(id)
                .map(ProductCategoryResponse::new)
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));
    }

    @Override
    public ProductCategoryResponse updateCategory(UUID id, ProductCategoryRequest request) {
        ProductCategory category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));

        ProductCategory parent = null;
        if (request.getParentCategoryId() != null) {
            parent = categoryRepository.findById(request.getParentCategoryId())
                    .orElseThrow(() -> new RuntimeException("Categoría padre no encontrada"));
        }

        category.setName(request.getName());
        category.setDescription(request.getDescription());
        category.setImageUrl(request.getImageUrl());
        category.setParentCategory(parent);

        return new ProductCategoryResponse(categoryRepository.save(category));
    }

    @Override
    public void deleteCategory(UUID id) {
        categoryRepository.deleteById(id);
    }
}
