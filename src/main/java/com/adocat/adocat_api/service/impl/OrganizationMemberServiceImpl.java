package com.adocat.adocat_api.service.impl;

import com.adocat.adocat_api.api.dto.organization.MemberRequest;
import com.adocat.adocat_api.api.dto.organization.MemberResponse;
import com.adocat.adocat_api.config.MailService;
import com.adocat.adocat_api.domain.entity.OrganizationMember;
import com.adocat.adocat_api.domain.entity.User;
import com.adocat.adocat_api.domain.repository.OrganizationMemberRepository;
import com.adocat.adocat_api.domain.repository.UserRepository;

import com.adocat.adocat_api.service.interfaces.OrganizationMemberService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class OrganizationMemberServiceImpl implements OrganizationMemberService {

    private final OrganizationMemberRepository memberRepository;
    private final UserRepository userRepository;
    private final MailService mailService;

    @Override
    public List<MemberResponse> getActiveMembersByOrganization(UUID organizationId) {
        return memberRepository.findAll().stream()
                .filter(m -> m.getOrganizationId().equals(organizationId) && m.isActive())
                .map(MemberResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public MemberResponse addMember(UUID organizationId, MemberRequest request, UUID currentUserId) {
        // Validar si el usuario actual es OWNER en esa organización
        validateOwnerPermissions(organizationId, currentUserId);

        UUID userId = request.getUserId();

        // Buscar si ya existe el miembro, si no, crearlo
        OrganizationMember member = memberRepository
                .findById(new OrganizationMember.OrganizationMemberId(organizationId, userId))
                .orElse(OrganizationMember.builder()
                        .organizationId(organizationId)
                        .userId(userId)
                        .joinedAt(Timestamp.from(Instant.now()))
                        .build());

        // Obtener el usuario completo desde la base de datos
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        // Establecer el objeto User en el miembro (evita el NullPointerException)
        member.setUser(user);

        // Asignar estado y rol
        member.setApproved(true);
        member.setActive(true);
        member.setRoleInOrg(request.getRoleInOrg());

        // Guardar el miembro actualizado
        memberRepository.save(member);

        // Enviar notificación por correo
        mailService.sendHtmlEmail(
                user.getEmail(),
                "Te han agregado a una organización en Adocat",
                "member-welcome.html",
                Map.of(
                        "name", user.getFirstName(),
                        "orgName", request.getOrganizationName()
                )
        );

        // Devolver respuesta formateada
        return MemberResponse.fromEntity(member);
    }


    @Override
    public void deactivateMember(UUID organizationId, UUID userId, UUID currentUserId) {
        validateOwnerPermissions(organizationId, currentUserId);
        OrganizationMember member = getMember(organizationId, userId);
        member.setActive(false);
        memberRepository.save(member);
    }

    @Override
    public MemberResponse reactivateMember(UUID organizationId, UUID userId, UUID currentUserId) {
        validateOwnerPermissions(organizationId, currentUserId);
        OrganizationMember member = getMember(organizationId, userId);
        member.setActive(true);
        member.setApproved(true);
        memberRepository.save(member);
        return MemberResponse.fromEntity(member);
    }

    private OrganizationMember getMember(UUID orgId, UUID userId) {
        return memberRepository.findById(new OrganizationMember.OrganizationMemberId(orgId, userId))
                .orElseThrow(() -> new IllegalArgumentException("Miembro no encontrado"));
    }

    private void validateOwnerPermissions(UUID organizationId, UUID currentUserId) {
        boolean isOwner = memberRepository.existsByOrganizationIdAndUserIdAndRoleInOrgAndApprovedTrueAndActiveTrue(
                organizationId, currentUserId, "OWNER"
        );
        if (!isOwner) {
            throw new SecurityException("Solo el propietario puede realizar esta acción.");
        }
    }
}