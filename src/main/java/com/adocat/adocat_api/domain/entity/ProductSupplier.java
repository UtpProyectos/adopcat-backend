package com.adocat.adocat_api.domain.entity;

import lombok.*;
import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "product_suppliers")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductSupplier {

    @Id
    @GeneratedValue
    private UUID supplierId;

    private String name;
    private String contactInfo;
}

