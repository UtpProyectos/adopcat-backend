package com.adocat.adocat_api.service.impl;

import com.adocat.adocat_api.api.dto.organization.OrganizationRequest;
import com.adocat.adocat_api.api.dto.organization.OrganizationResponse;
import com.adocat.adocat_api.api.dto.user.UserResponse;
import com.adocat.adocat_api.config.S3Service;
import com.adocat.adocat_api.domain.entity.Organization;
import com.adocat.adocat_api.domain.entity.OrganizationMember;
import com.adocat.adocat_api.domain.entity.User;
import com.adocat.adocat_api.domain.repository.OrganizationMemberRepository;
import com.adocat.adocat_api.domain.repository.OrganizationRepository;
import com.adocat.adocat_api.domain.repository.UserRepository;
import com.adocat.adocat_api.security.OrganizationAccessService;
import com.adocat.adocat_api.service.interfaces.IOrganizationService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.access.AccessDeniedException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class OrganizationServiceImpl implements IOrganizationService {

    private final OrganizationRepository organizationRepository;
    private final UserRepository userRepository; // Para buscar usuario creado por
    private final OrganizationMemberRepository organizationMemberRepository;
    private final OrganizationAccessService organizationAccessService;
    private final S3Service s3Service;

    @Override
    public OrganizationResponse createOrganization(OrganizationRequest request) {
        Organization org = mapRequestToEntity(request);

        org.setStatus(Organization.OrganizationStatus.SOLICITADO);
        org.setState(true);
        org.setVerified(false);
        org.setCreatedAt(LocalDateTime.now());

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User userPrincipal = (User) authentication.getPrincipal();
        String email = userPrincipal.getEmail();
        User user = userRepository.findByEmailWithRole(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        org.setCreatedBy(user);

        org = organizationRepository.save(org);

        // Crear al usuario como OWNER en organization_members
        OrganizationMember owner = OrganizationMember.builder()
                .organizationId(org.getOrganizationId())
                .userId(user.getUserId())
                .roleInOrg("OWNER")
                .approved(true)
                .active(true)
                .joinedAt(new Timestamp(System.currentTimeMillis()))
                .build();
        organizationMemberRepository.save(owner);

        return mapEntityToResponse(org);


    }


    @Override
    public OrganizationResponse updateOrganization(UUID organizationId, OrganizationRequest request) {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (!organizationAccessService.isOwnerOrAdmin(organizationId, currentUser)) {
            throw new AccessDeniedException("No tienes permiso para modificar esta organización");
        }

        Organization org = organizationRepository.findById(organizationId)
                .orElseThrow(() -> new EntityNotFoundException("Organization not found"));

        // Eliminar imagen si el frontend indica "null" explícito
        if ("null".equalsIgnoreCase(request.getCoverPhotoUrl()) && org.getCoverPhotoUrl() != null) {
            s3Service.deleteByUrl(org.getCoverPhotoUrl());
            org.setCoverPhotoUrl(null);
        }

        // Reemplazar si se sube nueva imagen
        if (request.getCoverPhoto() != null && !request.getCoverPhoto().isEmpty()) {
            if (org.getCoverPhotoUrl() != null) {
                s3Service.deleteByUrl(org.getCoverPhotoUrl());
            }

            String newUrl = s3Service.uploadFile(request.getCoverPhoto(), "organizations");
            org.setCoverPhotoUrl(newUrl);
        }

        // Actualizar campos
        org.setName(request.getName());
        org.setRuc(request.getRuc());
        org.setDescription(request.getDescription());
        org.setTipo(Organization.OrganizationType.valueOf(request.getTipo().toUpperCase()));
        org.setAddress(request.getAddress());
        org.setLatitude(request.getLatitude());
        org.setLongitude(request.getLongitude());

        org = organizationRepository.save(org);
        return mapEntityToResponse(org);
    }



    @Override
    public void deleteOrganization(UUID organizationId) {
        if (!organizationRepository.existsById(organizationId)) {
            throw new EntityNotFoundException("Organization not found");
        }
        organizationRepository.deleteById(organizationId);
    }

    @Override
    public OrganizationResponse getOrganizationById(UUID organizationId) {
        Organization org = organizationRepository.findById(organizationId)
                .orElseThrow(() -> new EntityNotFoundException("Organization not found"));
        return mapEntityToResponse(org);
    }

    @Override
    public List<OrganizationResponse> getAllOrganizations() {
        List<Organization> organizations = organizationRepository.findAll();
        return organizations.stream()
                .map(this::mapEntityToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<OrganizationResponse> getOrganizationsByUserId(UUID userId) {
        List<Organization> organizations = organizationRepository.findByCreatedByUserId(userId);
        return organizations.stream()
                .map(this::mapEntityToResponse)
                .collect(Collectors.toList());
    }


    // --- Mappers manuales ---
    private OrganizationResponse mapEntityToResponse(Organization org) {
        return OrganizationResponse.builder()
                .organizationId(org.getOrganizationId())
                .name(org.getName())
                .ruc(org.getRuc())
                .description(org.getDescription())
                .tipo(org.getTipo() != null ? org.getTipo().name() : null)
                .status(org.getStatus() != null ? org.getStatus().name() : null)
                .state(org.getState())
                .address(org.getAddress())
                .latitude(org.getLatitude())
                .longitude(org.getLongitude())
                .coverPhotoUrl(org.getCoverPhotoUrl())
                .verified(org.getVerified())
                .createdBy(mapUserToUserResponse(org.getCreatedBy()))
                .createdAt(org.getCreatedAt())
                .build();
    }

    private Organization mapRequestToEntity(OrganizationRequest request) {
        Organization org = new Organization();
        org.setName(request.getName());
        org.setRuc(request.getRuc());
        org.setDescription(request.getDescription());
        org.setTipo(request.getTipo() != null ? Organization.OrganizationType.valueOf(request.getTipo().toUpperCase()) : null);
        org.setAddress(request.getAddress());
        org.setLatitude(request.getLatitude());
        org.setLongitude(request.getLongitude());
        return org;
    }


    @Override
    public OrganizationResponse updateOrganizationStatus(UUID organizationId, Organization.OrganizationStatus status, Boolean verified) {
        Organization org = organizationRepository.findById(organizationId)
                .orElseThrow(() -> new EntityNotFoundException("Organization not found"));

        org.setStatus(status);
        org.setVerified(verified);

        // Puedes decidir activar o no dependiendo del status
        if (status == Organization.OrganizationStatus.APROBADO) {
            org.setState(true);
        }

        org = organizationRepository.save(org);
        return mapEntityToResponse(org);
    }

    @Override
    public OrganizationResponse updateOrganizationState(UUID organizationId, Boolean active) {
        Organization org = organizationRepository.findById(organizationId)
                .orElseThrow(() -> new EntityNotFoundException("Organization not found"));

        org.setState(active);

        org = organizationRepository.save(org);
        return mapEntityToResponse(org);
    }

    @Override
    public OrganizationResponse updateOrganizationApprovalStatusAndState(UUID organizationId, Boolean verified, Organization.OrganizationStatus status, Boolean active) {
        Organization org = organizationRepository.findById(organizationId)
                .orElseThrow(() -> new EntityNotFoundException("Organization not found"));

        if (verified != null) {
            org.setVerified(verified);
        }
        if (status != null) {
            org.setStatus(status);
        }
        if (active != null) {
            org.setState(active);
        }

        org = organizationRepository.save(org);
        return mapEntityToResponse(org);
    }

    private UserResponse mapUserToUserResponse(User user) {
        if (user == null) return null;

        return UserResponse.builder()
                .userId(user.getUserId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .profilePhoto(user.getProfilePhoto())
                .verified(user.getVerified())
                .build();
    }

}
