package com.adocat.adocat_api.domain.repository;


import com.adocat.adocat_api.domain.entity.Adoption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AdoptionRepository extends JpaRepository<Adoption, UUID> {
    // Ver solicitudes de un usuario (adoptante)
    List<Adoption> findByAdopterUserId(UUID adopterId);

    // Ver solicitudes de gatos de una organización
    List<Adoption> findByCat_Organization_OrganizationId(UUID organizationId);

    // Ver solicitudes por gato
    List<Adoption> findByCat_CatId(UUID catId);
    List<Adoption> findByAdopter_UserIdAndCat_CatId(UUID adopterId,UUID catId);
}
