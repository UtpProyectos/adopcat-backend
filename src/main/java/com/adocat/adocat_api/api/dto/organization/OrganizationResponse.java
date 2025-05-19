package com.adocat.adocat_api.api.dto.organization;

import com.adocat.adocat_api.api.dto.user.UserResponse;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class OrganizationResponse {
    private UUID organizationId;
    private String name;
    private String ruc;
    private String tipo;
    private String status;
    private Boolean state;
    private String description;
    private String address;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private String coverPhotoUrl;
    private Boolean verified;
    private UserResponse createdBy;
    private LocalDateTime createdAt;
}
