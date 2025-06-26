package com.adocat.adocat_api.service.interfaces;

import com.adocat.adocat_api.api.dto.request.ProductRequest;
import com.adocat.adocat_api.api.dto.response.ProductResponse;

import java.util.List;
import java.util.UUID;

public interface ProductService {
    ProductResponse createProduct(ProductRequest request);
    List<ProductResponse> getAllProducts();
    ProductResponse getProductById(UUID id);
    ProductResponse updateProduct(UUID id, ProductRequest request);
    ProductResponse updateVisibility(UUID id, boolean visible);
    void deleteProduct(UUID id);
}
