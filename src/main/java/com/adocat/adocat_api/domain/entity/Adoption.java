package com.adocat.adocat_api.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.sql.Timestamp;
import java.util.UUID;

@Entity
@Table(name = "adoption")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Adoption {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "request_id", updatable = false, nullable = false)
    private UUID requestId;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cat_id")
    private Cat cat;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "adopter_id")
    private User adopter;

    @Enumerated(EnumType.STRING) // ← MUY IMPORTANTE
    @Column(nullable = false, length = 20)
    private Status status;

    private Timestamp submittedAt;

    private Timestamp decisionDate;

    private boolean finalized;

    @Enumerated(EnumType.STRING)
    @Column(name = "interview_type", length = 20)
    private InterviewType interviewType;

    public enum InterviewType {
        PRESENTIAL,
        VIRTUAL,
        UNANSWERED // valor por defecto
    }

    /**
     * Estados posibles de una solicitud de adopción:
     * - PENDING: recién enviada, sin revisar.
     * - IN_REVIEW: en evaluación por la organización.
     * - APPROVED: aprobada por el refugio.
     * - REJECTED: rechazada.
     * - DELIVERED: gato entregado al adoptante.
     * - CANCELLED: cancelada por el adoptante u organización.
     */
    public enum Status {
        PENDING,
        IN_REVIEW,
        APPROVED,
        REJECTED,
        DELIVERED,
        IN_PROCESS_DELIVERY,
        CANCELLED
    }
}
