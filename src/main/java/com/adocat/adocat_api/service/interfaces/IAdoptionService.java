package com.adocat.adocat_api.service.interfaces;

import com.adocat.adocat_api.api.dto.adoption.AdoptionCommentResponse;
import com.adocat.adocat_api.api.dto.adoption.AdoptionRequest;
import com.adocat.adocat_api.api.dto.adoption.AdoptionResponse;
import com.adocat.adocat_api.domain.entity.User;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;


public interface IAdoptionService {
    void submitRequest(UUID catId, UUID adopterId, AdoptionRequest request,
                       MultipartFile receipt, MultipartFile homePhoto, MultipartFile commitment);

    List<AdoptionResponse> getUserRequests(UUID userId);

    List<AdoptionResponse> getOrganizationRequests(UUID organizationId);

    List<AdoptionCommentResponse> getComments(UUID requestId);

    void updateStatus(UUID requestId, String status);

    void leaveComment(UUID requestId, String comment, User user);

    void approveAdoption(UUID requestId);
    void updateInterviewType(UUID requestId, String interviewTypeStr);

    void deleteRequest(UUID requestId, UUID userId);
}
