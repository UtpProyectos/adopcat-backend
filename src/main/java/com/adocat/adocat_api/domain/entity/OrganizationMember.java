package com.adocat.adocat_api.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.UUID;


@Entity
@Table(name = "organization_members")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@IdClass(OrganizationMember.OrganizationMemberId.class)
public class OrganizationMember {

    @Id
    @Column(name = "organization_id")
    private UUID organizationId;

    @Id
    @Column(name = "user_id")
    private UUID userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;

    @Column(name = "role_in_org")
    private String roleInOrg;

    @Column
    private boolean approved;

    @Column
    private boolean active;

    @Column(name = "joined_at")
    private Timestamp joinedAt;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrganizationMemberId implements Serializable {
        private UUID organizationId;
        private UUID userId;
    }
}

