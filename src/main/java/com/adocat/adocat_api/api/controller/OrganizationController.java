package com.adocat.adocat_api.api.controller;

import com.adocat.adocat_api.api.dto.organization.OrganizationRequest;
import com.adocat.adocat_api.api.dto.organization.OrganizationResponse;
import com.adocat.adocat_api.domain.entity.Organization;
import com.adocat.adocat_api.domain.entity.User;
import com.adocat.adocat_api.security.OrganizationAccessService;
import com.adocat.adocat_api.service.interfaces.IOrganizationService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/organizations")
@RequiredArgsConstructor
public class OrganizationController {

    private final IOrganizationService organizationService;
    private final OrganizationAccessService organizationAccessService;

    @PostMapping
    public ResponseEntity<OrganizationResponse> createOrganization(@RequestBody OrganizationRequest request) {
        OrganizationResponse response = organizationService.createOrganization(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<OrganizationResponse> updateOrganization(
            @PathVariable UUID id,
            @RequestBody OrganizationRequest request
    ) {
        OrganizationResponse response = organizationService.updateOrganization(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrganization(@PathVariable UUID id) {
        organizationService.deleteOrganization(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrganizationResponse> getOrganizationById(
            @PathVariable UUID id,
            Authentication auth
    ) {
        User user = (User) auth.getPrincipal();
         organizationAccessService.isMemberOrOwner(id, user);

        OrganizationResponse response = organizationService.getOrganizationById(id);
        return ResponseEntity.ok(response);
    }


    @GetMapping
    public ResponseEntity<List<OrganizationResponse>> getAllOrganizations() {
        List<OrganizationResponse> organizations = organizationService.getAllOrganizations();
        return ResponseEntity.ok(organizations);
    }

    @GetMapping("/by-user/{userId}")
    public ResponseEntity<List<OrganizationResponse>> getByUser(@PathVariable UUID userId) {
        return ResponseEntity.ok(organizationService.getOrganizationsByUserId(userId));
    }


    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<OrganizationResponse> updateStatus(
            @PathVariable UUID id,
            @RequestParam Organization.OrganizationStatus status,
            @RequestParam Boolean verified
    ) {
        try {
            OrganizationResponse response = organizationService.updateOrganizationStatus(id, status, verified);
            return ResponseEntity.ok(response);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    // Activar / Desactivar organización (state)
    @PutMapping("/{id}/state")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<OrganizationResponse> updateState(
            @PathVariable UUID id,
            @RequestParam Boolean active
    ) {
        try {
            OrganizationResponse response = organizationService.updateOrganizationState(id, active);
            return ResponseEntity.ok(response);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    // Método unificado para actualizar verified, status y state juntos
    @PutMapping("/{id}/approval-status-state")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<OrganizationResponse> updateApprovalStatusAndState(
            @PathVariable UUID id,
            @RequestParam(required = false) Boolean verified,
            @RequestParam(required = false) Organization.OrganizationStatus status,
            @RequestParam(required = false) Boolean active
    ) {
        try {
            OrganizationResponse response = organizationService.updateOrganizationApprovalStatusAndState(id, verified, status, active);
            return ResponseEntity.ok(response);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
