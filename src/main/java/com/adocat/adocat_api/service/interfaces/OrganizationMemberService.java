package com.adocat.adocat_api.service.interfaces;


import com.adocat.adocat_api.api.dto.organization.MemberRequest;
import com.adocat.adocat_api.api.dto.organization.MemberResponse;

import java.util.List;
import java.util.UUID;

public interface OrganizationMemberService {
    List<MemberResponse> getActiveMembersByOrganization(UUID organizationId);
    MemberResponse addMember(UUID organizationId, MemberRequest request, UUID currentUserId);
    void deactivateMember(UUID organizationId, UUID userId, UUID currentUserId);
    MemberResponse reactivateMember(UUID organizationId, UUID userId, UUID currentUserId);
}