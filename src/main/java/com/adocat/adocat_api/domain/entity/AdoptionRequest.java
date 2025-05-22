package com.adocat.adocat_api.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.JdbcType;
import org.hibernate.type.descriptor.jdbc.UUIDJdbcType;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity(name = "adoption_requests")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class AdoptionRequest implements Serializable {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "request_id", columnDefinition = "UUID", updatable = false, nullable = false)
    @JdbcType(UUIDJdbcType.class)
    private UUID requestId;

    // Relaciones con Cat, User y Organization
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "cat_id", nullable = false)
    private Cat cat;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "adopter_id", nullable = false)
    private User adopter;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "organization_id", nullable = false)
    private Organization organization;

    @Column(length = 20, nullable = false)
    private String status; // PENDING, IN_REVIEW, APPROVED, REJECTED, DELIVERED, CANCELLED

    @Column(name = "submitted_at", nullable = false)
    private LocalDateTime submittedAt;

    @Column(name = "decision_date")
    private LocalDateTime decisionDate;

    @Column(nullable = false)
    private Boolean finalized;
}
