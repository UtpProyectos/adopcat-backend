package com.adocat.adocat_api.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.JdbcType;
import org.hibernate.type.descriptor.jdbc.UUIDJdbcType;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity(name = "organizations")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class Organization implements Serializable {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "organization_id", columnDefinition = "UUID", updatable = false, nullable = false)
    @JdbcType(UUIDJdbcType.class)
    private UUID organizationId;

    @Column(nullable = false)
    private String name;

    @Column(length = 11, unique = true)
    private String ruc;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private OrganizationType tipo;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private OrganizationStatus status = OrganizationStatus.SOLICITADO;

    @Column(nullable = false)
    private Boolean state = true; // true = activa, false = desactivada

    @Column(columnDefinition = "TEXT")
    private String address;

    @Column(precision = 10, scale = 6)
    private BigDecimal latitude;

    @Column(precision = 10, scale = 6)
    private BigDecimal longitude;

    @Column(name = "cover_photo_url")
    private String coverPhotoUrl;

    private Boolean verified;

    // Relación con User creada por
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    // Enumeraciones internas para tipo y status
    public enum OrganizationType {
        REFUGIO,
        CASA_HOGAR
    }

    public enum OrganizationStatus {
        SOLICITADO,
        PENDIENTE,
        EN_PROGRESO,
        APROBADO
    }
}
