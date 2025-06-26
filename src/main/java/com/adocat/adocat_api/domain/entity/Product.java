package com.adocat.adocat_api.domain.entity;

import lombok.*;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "products")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Product {
    @Id
    @GeneratedValue
    private UUID productId;

    private String name;
    private String description;
    private BigDecimal price;
    private BigDecimal discountPct;
    private String imageUrl;
    private Boolean isActive;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private ProductCategory category;

    @ManyToOne
    @JoinColumn(name = "supplier_id")
    private ProductSupplier supplier;

}
