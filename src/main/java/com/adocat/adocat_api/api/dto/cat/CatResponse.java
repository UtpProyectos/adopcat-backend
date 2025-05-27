package com.adocat.adocat_api.api.dto.cat;

import com.adocat.adocat_api.api.dto.user.UserResponse;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CatResponse {

    private UUID catId;

    private String name;

    private LocalDate birthDate;

    private String gender;

    private String size;

    private String healthStatus;

    private String raza;

    private String description;

    private String location;

    private BigDecimal latitude;

    private BigDecimal longitude;

    private String status;

    private LocalDateTime publishedAt;

    private String mainImageUrl;

    private UserResponse createdBy;

    private UUID organizationId;

    private UUID sentToOrg;

    private UUID adoptedBy;

    private LocalDateTime adoptedAt;

    private UUID adoptionRequestId;

    private List<CatFeatureRequest> features;
}