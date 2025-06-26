package com.adocat.adocat_api.service.impl;

import com.adocat.adocat_api.api.dto.request.ProductRequest;
import com.adocat.adocat_api.api.dto.response.ProductResponse;
import com.adocat.adocat_api.domain.entity.Product;
import com.adocat.adocat_api.domain.repository.ProductCategoryRepository;
import com.adocat.adocat_api.domain.repository.ProductRepository;
import com.adocat.adocat_api.domain.repository.ProductSupplierRepository;
import com.adocat.adocat_api.service.interfaces.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductCategoryRepository categoryRepository;
    private final ProductSupplierRepository supplierRepository;

    @Override
    public ProductResponse createProduct(ProductRequest request) {
        var category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));
        var supplier = supplierRepository.findById(request.getSupplierId())
                .orElseThrow(() -> new RuntimeException("Proveedor no encontrado"));

        Product product = Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .discountPct(request.getDiscountPct())
                .imageUrl(request.getImageUrl())
                .category(category)
                .supplier(supplier)
                .isActive(true)
                .build();

        return new ProductResponse(productRepository.save(product));
    }

    @Override
    public List<ProductResponse> getAllProducts() {
        return productRepository.findAll().stream()
                .map(ProductResponse::new)
                .collect(Collectors.toList());
    }

    @Override
    public ProductResponse getProductById(UUID id) {
        return productRepository.findById(id)
                .map(ProductResponse::new)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
    }

    @Override
    public ProductResponse updateProduct(UUID id, ProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        var category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));
        var supplier = supplierRepository.findById(request.getSupplierId())
                .orElseThrow(() -> new RuntimeException("Proveedor no encontrado"));

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setDiscountPct(request.getDiscountPct());
        product.setImageUrl(request.getImageUrl());
        product.setCategory(category);
        product.setSupplier(supplier);

        return new ProductResponse(productRepository.save(product));
    }

    @Override
    public ProductResponse updateVisibility(UUID id, boolean visible) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        product.setIsActive(visible);

        return new ProductResponse(productRepository.save(product));
    }

    @Override
    public void deleteProduct(UUID id) {
        productRepository.deleteById(id);
    }
}
