package com.adocat.adocat_api.service.interfaces;

import com.adocat.adocat_api.api.dto.request.ProductCategoryRequest;
import com.adocat.adocat_api.api.dto.response.ProductCategoryResponse;

import java.util.List;
import java.util.UUID;

public interface ProductCategoryService {
    ProductCategoryResponse createCategory(ProductCategoryRequest request);
    List<ProductCategoryResponse> getAllCategories();
    ProductCategoryResponse getCategoryById(UUID id);
    ProductCategoryResponse updateCategory(UUID id, ProductCategoryRequest request);
    void deleteCategory(UUID id);
}
