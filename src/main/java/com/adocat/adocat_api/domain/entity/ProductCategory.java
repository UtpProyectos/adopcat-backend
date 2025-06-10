package com.adocat.adocat_api.domain.entity;

import lombok.*;
import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "product_categories")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductCategory {

    @Id
    @GeneratedValue
    private UUID categoryId;

    private String name;
    private String description;
    private String imageUrl;

    @ManyToOne
    @JoinColumn(name = "parent_category_id")
    private ProductCategory parentCategory;
}
