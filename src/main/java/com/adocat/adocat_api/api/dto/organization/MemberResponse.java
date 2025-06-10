package com.adocat.adocat_api.api.dto.organization;
import com.adocat.adocat_api.domain.entity.OrganizationMember;
import lombok.Builder;
import lombok.Data;

import java.sql.Timestamp;
import java.util.UUID;

@Data
@Builder
public class MemberResponse {
    private UUID userId;
    private String userFullName;
    private String userEmail;
    private String roleInOrg;
    private boolean approved;
    private boolean active;
    private Timestamp joinedAt;
    private String profilePhoto;

    public static MemberResponse fromEntity(OrganizationMember member) {
        return MemberResponse.builder()
                .userId(member.getUserId())
                .userFullName(member.getUser().getFirstName() + " " + member.getUser().getLastName())
                .userEmail(member.getUser().getEmail())
                .roleInOrg(member.getRoleInOrg())
                .approved(member.isApproved())
                .active(member.isActive())
                .joinedAt(member.getJoinedAt())
                .profilePhoto(member.getUser().getProfilePhoto())
                .build();
    }
}
