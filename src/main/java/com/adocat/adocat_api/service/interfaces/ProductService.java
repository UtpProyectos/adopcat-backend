package com.adocat.adocat_api.service.interfaces;

import com.adocat.adocat_api.api.dto.ProductDto;
import java.util.List;

public interface ProductService {
    List<ProductDto> getAllProducts();
    ProductDto createProduct(ProductDto productDto);
}
