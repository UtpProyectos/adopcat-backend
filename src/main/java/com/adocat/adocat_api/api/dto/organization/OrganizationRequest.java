package com.adocat.adocat_api.api.dto.organization;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrganizationRequest {
    private String name;
    private String ruc;
    private String tipo;
    private String description;
    private String address;
    private BigDecimal latitude;
    private BigDecimal longitude;
}
