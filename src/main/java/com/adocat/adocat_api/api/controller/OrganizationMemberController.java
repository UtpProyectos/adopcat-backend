package com.adocat.adocat_api.api.controller;

import com.adocat.adocat_api.api.dto.organization.MemberRequest;
import com.adocat.adocat_api.api.dto.organization.MemberResponse;
import com.adocat.adocat_api.domain.entity.User;
import com.adocat.adocat_api.service.interfaces.OrganizationMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/organizations/{organizationId}/members")
@RequiredArgsConstructor
public class OrganizationMemberController {

    private final OrganizationMemberService memberService;

    @GetMapping
    public ResponseEntity<List<MemberResponse>> getMembers(
            @PathVariable UUID organizationId
    ) {
        List<MemberResponse> members = memberService.getActiveMembersByOrganization(organizationId);
        return ResponseEntity.ok(members);
    }

    @PostMapping
    public ResponseEntity<MemberResponse> addMember(
            @PathVariable UUID organizationId,
            @RequestBody MemberRequest request,
            @AuthenticationPrincipal User currentUser
    ) {
        MemberResponse added = memberService.addMember(organizationId, request, currentUser.getUserId());
        return ResponseEntity.ok(added);
    }

    @PutMapping("/{userId}/deactivate")
    public ResponseEntity<Void> deactivateMember(
            @PathVariable UUID organizationId,
            @PathVariable UUID userId,
            @AuthenticationPrincipal User currentUser
    ) {
        memberService.deactivateMember(organizationId, userId, currentUser.getUserId());
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{userId}/reactivate")
    public ResponseEntity<MemberResponse> reactivateMember(
            @PathVariable UUID organizationId,
            @PathVariable UUID userId,
            @AuthenticationPrincipal User currentUser
    ) {
        MemberResponse updated = memberService.reactivateMember(organizationId, userId, currentUser.getUserId());
        return ResponseEntity.ok(updated);
    }
}
