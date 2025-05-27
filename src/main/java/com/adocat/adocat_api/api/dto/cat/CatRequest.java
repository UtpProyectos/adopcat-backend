package com.adocat.adocat_api.api.dto.cat;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CatRequest {

    private String name;

    private LocalDate birthDate;

    private String gender;

    private String size; // "SMALL", "MEDIUM", "LARGE"

    private String healthStatus;

    private String raza;

    private String description;

    private String location;

    private BigDecimal latitude;

    private BigDecimal longitude;

    private String mainImageUrl;

    private UUID createdBy;

    // Nuevo campo opcional para asignar organización
    private UUID organizationId;


    private List<CatFeatureRequest> features;
}
