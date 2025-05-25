package com.adocat.adocat_api.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.JdbcType;
import org.hibernate.type.descriptor.jdbc.UUIDJdbcType;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity(name = "cats")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class Cat implements Serializable {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "cat_id", columnDefinition = "UUID", updatable = false, nullable = false)
    @JdbcType(UUIDJdbcType.class)
    private UUID catId;

    @Column(nullable = false)
    private String name;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(length = 10)
    private String gender;

    @Column(length = 10)
    private String size; // "SMALL", "MEDIUM", "LARGE"

    @Column(name = "health_status", columnDefinition = "TEXT")
    private String healthStatus;

    @Column(length = 100)
    private String raza;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "TEXT")
    private String location;

    @Column(precision = 10, scale = 6)
    private BigDecimal latitude;

    @Column(precision = 10, scale = 6)
    private BigDecimal longitude;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private CatStatus status;

    @Column(name = "published_at")
    private LocalDateTime publishedAt;

    @Column(name = "main_image_url", length = 500)
    private String mainImageUrl;

    // Relaciones con otras entidades

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "organization_id", nullable = true)
    private Organization organization;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "sent_to_org", nullable = true)
    private Organization sentToOrg;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "adopted_by", nullable = true)
    private User adoptedBy;

    @Column(name = "adopted_at")
    private LocalDateTime adoptedAt;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "adoption_request_id", nullable = true)
    private AdoptionRequest adoptionRequest;

    public enum CatStatus {
        AVAILABLE, // El gato está disponible para adopción
        IN_ADOPTION, // El gato está en proceso de adopción
        ADOPTED,// El gato ya fue adoptado y no está disponible.
        NOT_AVAILABLE,// NO DISPONIBLE POR ALGUNOS PROBLEMAS DEL GATO
        SENTO_ORGANIZATION,// GATO ENVIANDO A ORGANIZACION
    }

}
