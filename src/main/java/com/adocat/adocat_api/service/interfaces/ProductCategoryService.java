package com.adocat.adocat_api.service.interfaces;

import com.adocat.adocat_api.api.dto.ProductCategoryDto;
import java.util.List;

public interface ProductCategoryService {
    List<ProductCategoryDto> getAllCategories();
    ProductCategoryDto createCategory(ProductCategoryDto dto);
}
