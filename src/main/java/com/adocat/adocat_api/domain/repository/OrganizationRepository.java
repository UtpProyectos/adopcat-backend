package com.adocat.adocat_api.domain.repository;

import com.adocat.adocat_api.api.dto.organization.OrganizationResponse;
import com.adocat.adocat_api.domain.entity.Organization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OrganizationRepository extends JpaRepository<Organization, UUID> {
    List<Organization> findByCreatedByUserId(UUID userId);
}
