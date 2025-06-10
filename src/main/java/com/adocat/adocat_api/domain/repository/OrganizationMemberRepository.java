package com.adocat.adocat_api.domain.repository;

import com.adocat.adocat_api.domain.entity.OrganizationMember;
import com.adocat.adocat_api.domain.entity.OrganizationMember.OrganizationMemberId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface OrganizationMemberRepository extends JpaRepository<OrganizationMember, OrganizationMemberId> {
    boolean existsByOrganizationIdAndUserIdAndApprovedTrueAndActiveTrue(UUID organizationId, UUID userId);

    boolean existsByOrganizationIdAndUserIdAndRoleInOrgAndApprovedTrueAndActiveTrue(UUID organizationId, UUID userId, String roleInOrg);

    List<OrganizationMember> findByUserIdAndActiveTrueAndApprovedTrue(UUID userId);


}