package com.adocat.adocat_api.security;

import com.adocat.adocat_api.domain.repository.OrganizationMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import java.util.UUID;
import com.adocat.adocat_api.domain.entity.User;

@Service
@RequiredArgsConstructor
public class OrganizationAccessService {

    private final OrganizationMemberRepository organizationMemberRepository;

    public boolean isOwnerOrAdmin(UUID organizationId, User user) {
        if (user.getRole().getRoleName().equalsIgnoreCase("ADMIN")) {
            return true;
        }
        return organizationMemberRepository.existsByOrganizationIdAndUserIdAndRoleInOrgAndApprovedTrueAndActiveTrue(
                organizationId, user.getUserId(), "OWNER");
    }

    public boolean isMember(UUID organizationId, UUID userId) {
        return organizationMemberRepository.existsByOrganizationIdAndUserIdAndApprovedTrueAndActiveTrue(
                organizationId, userId);
    }

    public void isMemberOrOwner(UUID organizationId, User user) {
        boolean isAuthorized = organizationMemberRepository
                .existsByOrganizationIdAndUserIdAndApprovedTrueAndActiveTrue(organizationId, user.getUserId());

        if (!isAuthorized) {
            throw new AccessDeniedException("No tienes acceso a esta organización");
        }
    }

}
