package com.adocat.adocat_api.domain.repository;

import com.adocat.adocat_api.domain.entity.Cat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CatRepository extends JpaRepository<Cat, UUID> {
    List<Cat> findByStatus(Cat.CatStatus status);
    List<Cat> findByOrganization_OrganizationId(UUID organizationId);

}
