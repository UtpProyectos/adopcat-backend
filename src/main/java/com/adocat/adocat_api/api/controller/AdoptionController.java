package com.adocat.adocat_api.api.controller;

import com.adocat.adocat_api.api.dto.adoption.AdoptionCommentResponse;
import com.adocat.adocat_api.api.dto.adoption.AdoptionRequest;
import com.adocat.adocat_api.api.dto.adoption.AdoptionResponse;
import com.adocat.adocat_api.domain.entity.User;
import com.adocat.adocat_api.domain.repository.OrganizationMemberRepository;
import com.adocat.adocat_api.security.OrganizationAccessService;
import com.adocat.adocat_api.service.interfaces.IAdoptionService;
import jakarta.annotation.security.RolesAllowed;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/adoptions")
@RequiredArgsConstructor
public class AdoptionController {

    private final IAdoptionService adoptionService;
    private final OrganizationMemberRepository organizationMemberRepository;
    private final OrganizationAccessService organizationAccessService;


    // ✅ 1. Registrar solicitud de adopción (usuario)
    @PostMapping(
            value = "/{catId}/request",
            consumes = "multipart/form-data"
    )
    @RolesAllowed({"ADOPTANTE"})
    public ResponseEntity<?> submitRequest(
            @PathVariable UUID catId,
            @RequestPart("data") AdoptionRequest request,
            @RequestPart("receipt") MultipartFile receipt,
            @RequestPart("homePhoto") MultipartFile homePhoto,
            @RequestPart("commitment") MultipartFile commitment,
            Authentication auth
    ) {
        User user = (User) auth.getPrincipal();
        UUID userId = user.getUserId();
        adoptionService.submitRequest(catId, userId, request, receipt, homePhoto, commitment);
        return ResponseEntity.ok("Solicitud registrada exitosamente.");
    }

    // ✅ 2. Obtener solicitudes del usuario autenticado
    @GetMapping("/my-requests")
    @RolesAllowed({"ADOPTANTE"})
    public ResponseEntity<List<AdoptionResponse>> getMyRequests(Authentication auth) {
        User user = (User) auth.getPrincipal();
        UUID userId = user.getUserId();
        return ResponseEntity.ok(adoptionService.getUserRequests(userId));
    }

    // ✅ 3. Obtener solicitudes de la organización autenticada
    @GetMapping("/organization/{organizationId}")
    @RolesAllowed({"ADMIN", "RESCATISTA"})
    public ResponseEntity<List<AdoptionResponse>> getRequestsByOrg(
            @PathVariable UUID organizationId,
            Authentication auth
    ) {
        User user = (User) auth.getPrincipal();
        organizationAccessService.isMemberOrOwner(organizationId, user);

        return ResponseEntity.ok(adoptionService.getOrganizationRequests(organizationId));
    }

    // ✅ 4. Cambiar estado de solicitud (PENDING → APPROVED/REJECTED/IN_REVIEW)
    @PatchMapping("/{requestId}/status")
    @RolesAllowed({"ADMIN", "RESCATISTA"})
    public ResponseEntity<?> updateStatus(
            @PathVariable UUID requestId,
            @RequestParam("status") String status
    ) {
        adoptionService.updateStatus(requestId, status);
        return ResponseEntity.ok("Estado actualizado a: " + status);
    }

    @PutMapping("/{requestId}/interview-type")
    public ResponseEntity<Void> updateInterviewType(
            @PathVariable UUID requestId,
            @RequestBody String type
    ) {
        adoptionService.updateInterviewType(requestId, type.replace("\"", ""));
        return ResponseEntity.noContent().build();
    }



    // ✅ 5. Agregar comentario a una solicitud
    @PostMapping("/{requestId}/comment")
    public ResponseEntity<?> comment(
            @PathVariable UUID requestId,
            @RequestParam("comment") String comment,
            Authentication auth
    ) {
        User user = (User) auth.getPrincipal();
        adoptionService.leaveComment(requestId, comment, user);
        return ResponseEntity.ok("Comentario registrado.");
    }


    @GetMapping("/{requestId}/comments")
    public ResponseEntity<List<AdoptionCommentResponse>> getComments(@PathVariable UUID requestId) {
        return ResponseEntity.ok(adoptionService.getComments(requestId));
    }


    // ✅ 6. Aprobar adopción (finalizar y marcar como DELIVERED)
    @PatchMapping("/{requestId}/approve")
    @RolesAllowed({"ADMIN", "RESCATISTA"})
    public ResponseEntity<?> finalizeAdoption(@PathVariable UUID requestId) {
        adoptionService.approveAdoption(requestId);
        return ResponseEntity.ok("Adopción finalizada y marcada como entregada.");
    }

    @DeleteMapping("/{requestId}")
    @RolesAllowed("ADOPTANTE")
    public ResponseEntity<?> deleteRequest(@PathVariable UUID requestId, Authentication auth) {
        User user = (User) auth.getPrincipal();
        UUID userId = user.getUserId();
        adoptionService.deleteRequest(requestId, userId);
        return ResponseEntity.noContent().build();
    }

}
