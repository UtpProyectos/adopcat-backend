package com.adocat.adocat_api.domain.repository;

import com.adocat.adocat_api.domain.entity.ProductSupplier;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProductSupplierRepository extends JpaRepository<ProductSupplier, UUID> {
}
