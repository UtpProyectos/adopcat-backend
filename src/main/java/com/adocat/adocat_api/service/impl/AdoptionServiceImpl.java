package com.adocat.adocat_api.service.impl;

import com.adocat.adocat_api.api.dto.adoption.AdoptionCommentResponse;
import com.adocat.adocat_api.api.dto.adoption.AdoptionRequest;
import com.adocat.adocat_api.api.dto.adoption.AdoptionResponse;
import com.adocat.adocat_api.api.dto.cat.CatResponse;
import com.adocat.adocat_api.api.dto.organization.OrganizationResponse;
import com.adocat.adocat_api.api.dto.user.UserResponse;
import com.adocat.adocat_api.domain.entity.Adoption;
import com.adocat.adocat_api.domain.entity.Adoption.Status;
import com.adocat.adocat_api.domain.entity.Cat;
import com.adocat.adocat_api.domain.entity.User;
import com.adocat.adocat_api.domain.repository.AdoptionRepository;
import com.adocat.adocat_api.domain.repository.CatRepository;
import com.adocat.adocat_api.domain.repository.UserRepository;
import com.adocat.adocat_api.service.interfaces.IAdoptionService;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.cloud.FirestoreClient;
import com.adocat.adocat_api.config.S3Service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class AdoptionServiceImpl implements IAdoptionService {

    private final AdoptionRepository adoptionRepository;
    private final CatRepository catRepository;
    private final UserRepository userRepository;
    private final S3Service s3Service;

    @Override
    public void submitRequest(UUID catId, UUID adopterId, AdoptionRequest request,
                              MultipartFile receipt, MultipartFile homePhoto, MultipartFile commitment) {

        List<Adoption> existingRequests = adoptionRepository
                .findByAdopter_UserIdAndCat_CatId(adopterId, catId);

        boolean hasActiveRequest = existingRequests.stream()
                .anyMatch(req -> req.getStatus() != Status.CANCELLED);

        if (hasActiveRequest) {
            throw new RuntimeException("Ya existe una solicitud activa de adopción para este gato.");
        }


        Cat cat = catRepository.findById(catId)
                .orElseThrow(() -> new RuntimeException("Gato no encontrado"));
        User adopter = userRepository.findById(adopterId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Timestamp now = Timestamp.from(Instant.now());

        // Primero se guarda la solicitud en la BD para obtener el requestId generado
        Adoption adoption = Adoption.builder()
                .cat(cat)
                .adopter(adopter)
                .status(Status.PENDING)
                .submittedAt(now)
                .finalized(false)
                .build();

        adoption = adoptionRepository.save(adoption); // Aquí se genera el UUID
        UUID requestId = adoption.getRequestId();


        // Subir archivos a Cloudinary
        String receiptUrl = s3Service.uploadFile(receipt, "adoption/" + requestId + "/receipt");
        String homePhotoUrl = s3Service.uploadFile(homePhoto, "adoption/" + requestId + "/homePhoto");
        String commitmentUrl = s3Service.uploadFile(commitment, "adoption/" + requestId + "/commitment");

        // Guardar en Firestore
        Firestore db = FirestoreClient.getFirestore();
        Map<String, Object> formData = new HashMap<>();
        formData.put("reason", request.getReason());
        formData.put("experience", request.getExperience());
        formData.put("residenceType", request.getResidenceType());
        formData.put("reactionPlan", request.getReactionPlan());
        formData.put("followUpConsent", request.getFollowUpConsent());
        formData.put("receiptUrl", receiptUrl);
        formData.put("homePhotoUrl", homePhotoUrl);
        formData.put("commitmentUrl", commitmentUrl);
        formData.put("submittedAt", now);

        db.collection("adoption_questionnaire").document(requestId.toString()).set(formData);


    }

    @Override
    public List<AdoptionResponse> getUserRequests(UUID userId) {
        return adoptionRepository.findByAdopterUserId(userId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public List<AdoptionResponse> getOrganizationRequests(UUID organizationId) {
        return adoptionRepository.findByCat_Organization_OrganizationId(organizationId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public void updateStatus(UUID requestId, String statusStr) {
        Adoption adoption = adoptionRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));

        Status status = Status.valueOf(statusStr.toUpperCase()); // Convierte string a enum
        adoption.setStatus(status);

        if (status == Status.APPROVED || status == Status.REJECTED) {
            adoption.setDecisionDate(Timestamp.from(Instant.now()));
        }

        adoptionRepository.save(adoption);
    }

    @Override
    public void updateInterviewType(UUID requestId, String interviewTypeStr) {
        Adoption adoption = adoptionRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));

        Adoption.InterviewType interviewType = Adoption.InterviewType.valueOf(interviewTypeStr.toUpperCase());
        adoption.setInterviewType(interviewType);

        adoptionRepository.save(adoption);
    }



    @Override
    public void leaveComment(UUID requestId, String comment, User user) {
        Firestore db = FirestoreClient.getFirestore();
        Map<String, Object> commentData = new HashMap<>();
        commentData.put("comment", comment);
        commentData.put("createdAt", Timestamp.from(Instant.now()));
        commentData.put("userName", user.getFirstName() + " " + user.getLastName());
        commentData.put("profilePhoto", user.getProfilePhoto());

        db.collection("adoption_comments")
                .document(requestId.toString())
                .collection("comments")
                .add(commentData);
    }


    @Override
    public List<AdoptionCommentResponse> getComments(UUID requestId) {
        Firestore db = FirestoreClient.getFirestore();
        try {
            return db.collection("adoption_comments")
                    .document(requestId.toString())
                    .collection("comments")
                    .orderBy("createdAt")
                    .get()
                    .get()
                    .getDocuments()
                    .stream()
                    .map(doc -> new AdoptionCommentResponse(
                            doc.getString("comment"),
                            doc.getString("userName"),
                            doc.getString("profilePhoto"),
                            doc.getTimestamp("createdAt").toDate().toInstant()
                    ))
                    .toList();
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener comentarios", e);
        }
    }


    @Override
    @Transactional
    public void approveAdoption(UUID requestId) {
        Adoption adoption = adoptionRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));

        adoption.setStatus(Status.DELIVERED);
        adoption.setFinalized(true);
        adoption.setDecisionDate(Timestamp.from(Instant.now()));
        adoption = adoptionRepository.save(adoption);

        // 💥 Aquí forzamos el flush para asegurar que la adopción está en la BD
        adoptionRepository.flush();

        Cat cat = catRepository.findById(adoption.getCat().getCatId())
                .orElseThrow(() -> new RuntimeException("Gato no encontrado"));

        cat.setStatus(Cat.CatStatus.ADOPTED);
        cat.setAdoptedAt(LocalDateTime.now());
        cat.setAdoptedBy(adoption.getAdopter());
        cat.setAdoptionRequest(adoption);

        catRepository.save(cat);
    }



    @Override
    public void deleteRequest(UUID requestId, UUID userId) {
        Adoption adoption = adoptionRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));

        if (!adoption.getAdopter().getUserId().equals(userId)) {
            throw new RuntimeException("No autorizado para eliminar esta solicitud");
        }

        if (adoption.getStatus() != Status.PENDING) {
            throw new RuntimeException("Solo se pueden eliminar solicitudes en estado PENDING");
        }

        adoptionRepository.delete(adoption);
    }

//    private AdoptionResponse toResponse(Adoption adoption) {
//        return AdoptionResponse.builder()
//                .requestId(adoption.getRequestId())
//                .status(adoption.getStatus().name())
//                .finalized(adoption.isFinalized())
//                .submittedAt(adoption.getSubmittedAt())
//                .decisionDate(adoption.getDecisionDate())
//                .cat(CatResponse.builder()
//                        .catId(adoption.getCat().getCatId())
//                        .name(adoption.getCat().getName())
//                        .status(adoption.getCat().getStatus().name())
//                        .publishedAt(adoption.getCat().getPublishedAt())
//                        .mainImageUrl(adoption.getCat().getMainImageUrl())
//                        .organization(OrganizationResponse.builder()
//                                .name(adoption.getCat().getOrganization().getName())
//                                .build()
//                        )
//                        .build())
//                .adopter(UserResponse.builder()
//                        .userId(adoption.getAdopter().getUserId())
//                        .firstName(adoption.getAdopter().getFirstName())
//                        .lastName(adoption.getAdopter().getLastName())
//                        .email(adoption.getAdopter().getEmail())
//                        .profilePhoto(adoption.getAdopter().getProfilePhoto())
//                        .verified(adoption.getAdopter().getVerified())
//                        .dni(adoption.getAdopter().getDni())
//                        .phoneNumber(adoption.getAdopter().getPhoneNumber())
//                        .address(adoption.getAdopter().getAddress())
//                        .build())
//                .build();
//    }

    private AdoptionResponse toResponse(Adoption adoption) {
        AdoptionResponse response = AdoptionResponse.builder()
                .requestId(adoption.getRequestId())
                .status(adoption.getStatus().name())
                .finalized(adoption.isFinalized())
                .submittedAt(adoption.getSubmittedAt())
                .decisionDate(adoption.getDecisionDate())
                .interviewType(adoption.getInterviewType())
                .cat(CatResponse.builder()
                        .catId(adoption.getCat().getCatId())
                        .name(adoption.getCat().getName())
                        .status(adoption.getCat().getStatus().name())
                        .publishedAt(adoption.getCat().getPublishedAt())
                        .mainImageUrl(adoption.getCat().getMainImageUrl())
                        .organization(OrganizationResponse.builder()
                                .name(adoption.getCat().getOrganization().getName())
                                .build())
                        .build())
                .adopter(UserResponse.builder()
                        .userId(adoption.getAdopter().getUserId())
                        .firstName(adoption.getAdopter().getFirstName())
                        .lastName(adoption.getAdopter().getLastName())
                        .email(adoption.getAdopter().getEmail())
                        .profilePhoto(adoption.getAdopter().getProfilePhoto())
                        .verified(adoption.getAdopter().getVerified())
                        .dni(adoption.getAdopter().getDni())
                        .phoneNumber(adoption.getAdopter().getPhoneNumber())
                        .address(adoption.getAdopter().getAddress())
                        .build())
                .build();

        // Enriquecer con datos del formulario en Firestore
        try {
            Firestore db = FirestoreClient.getFirestore();
            DocumentSnapshot doc = db.collection("adoption_questionnaire")
                    .document(adoption.getRequestId().toString())
                    .get()
                    .get();

            if (doc.exists()) {
                Map<String, Object> data = doc.getData();
                if (data != null) {
                    response.setReason((String) data.get("reason"));
                    response.setExperience((String) data.get("experience"));
                    response.setResidenceType((String) data.get("residenceType"));
                    response.setReactionPlan((String) data.get("reactionPlan"));

                    response.setReceiptUrl((String) data.get("receiptUrl"));
                    response.setHomePhotoUrl((String) data.get("homePhotoUrl"));
                    response.setCommitmentUrl((String) data.get("commitmentUrl"));

                    Object rawConsent = data.get("followUpConsent");
                    if (rawConsent != null) {
                        response.setFollowUpConsent(Boolean.parseBoolean(rawConsent.toString()));
                    }

                }
            }
        } catch (Exception e) {
            System.err.println("Error al cargar detalles del formulario desde Firestore: " + e.getMessage());
        }

        return response;
    }

}
