package com.adocat.adocat_api.domain.repository;

import com.adocat.adocat_api.domain.entity.ProductCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface ProductCategoryRepository extends JpaRepository<ProductCategory, UUID> {
}
