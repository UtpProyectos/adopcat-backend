package com.adocat.adocat_api.service.interfaces;

import com.adocat.adocat_api.api.dto.organization.OrganizationRequest;
import com.adocat.adocat_api.api.dto.organization.OrganizationResponse;
import com.adocat.adocat_api.domain.entity.Organization;

import java.util.List;
import java.util.UUID;

public interface IOrganizationService {

    OrganizationResponse createOrganization(OrganizationRequest request);

    OrganizationResponse updateOrganization(UUID organizationId, OrganizationRequest request);

    void deleteOrganization(UUID organizationId);

    OrganizationResponse getOrganizationById(UUID organizationId);

    List<OrganizationResponse> getAllOrganizations();

    OrganizationResponse updateOrganizationStatus(UUID organizationId, Organization.OrganizationStatus status, Boolean verified);

    OrganizationResponse updateOrganizationState(UUID organizationId, Boolean active);

    OrganizationResponse updateOrganizationApprovalStatusAndState(UUID organizationId, Boolean verified, Organization.OrganizationStatus status, Boolean active);

}
