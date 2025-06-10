package com.adocat.adocat_api.api.dto.organization;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class MemberRequest {
    private UUID userId;              // ID del usuario a agregar
    private String roleInOrg;         // OWNER, VOLUNTARIO, etc.
    private String organizationName;  // Para el correo (opcional si quieres mostrarlo)
}
