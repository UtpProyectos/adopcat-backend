package com.adocat.adocat_api.service.impl;

import com.adocat.adocat_api.api.dto.cat.CatFeatureRequest;
import com.adocat.adocat_api.api.dto.cat.CatRequest;
import com.adocat.adocat_api.api.dto.cat.CatResponse;
import com.adocat.adocat_api.api.dto.user.UserResponse;
import com.adocat.adocat_api.config.CloudinaryService;
import com.adocat.adocat_api.domain.entity.Cat;
import com.adocat.adocat_api.domain.entity.User;
import com.adocat.adocat_api.domain.entity.Organization;
import com.adocat.adocat_api.domain.repository.AdoptionRequestRepository;
import com.adocat.adocat_api.domain.repository.CatRepository;
import com.adocat.adocat_api.domain.repository.UserRepository;
import com.adocat.adocat_api.domain.repository.OrganizationRepository;
import com.adocat.adocat_api.service.interfaces.ICatFeatureService;
import com.adocat.adocat_api.service.interfaces.ICatService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CatServiceImpl implements ICatService {

    private final CatRepository catRepository;
    private final UserRepository userRepository;
    private final OrganizationRepository organizationRepository;
    private final AdoptionRequestRepository adoptionRequestRepository;
    private final CloudinaryService cloudinaryService;
    private final ICatFeatureService catFeatureService;

    @Override
    public List<CatResponse> getAllCats() {
        return catRepository.findByStatus(Cat.CatStatus.AVAILABLE)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<CatResponse> getAllCatsByOrganization(UUID organizationId) {
        return catRepository.findByOrganization_OrganizationId(organizationId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public CatResponse getCatById(UUID catId) {
        Cat cat = catRepository.findById(catId)
                .orElseThrow(() -> new EntityNotFoundException("Cat not found with id " + catId));
        return mapToResponse(cat);
    }

    @Override
    public CatResponse createCat(CatRequest catRequest, MultipartFile file) {
        // 1. Subir imagen si llega archivo
        if (file != null && !file.isEmpty()) {
            String imageUrl = cloudinaryService.uploadFile(file, "cats");
            catRequest.setMainImageUrl(imageUrl);
        }

        // 2. Mapear entidad
        Cat cat = mapToEntity(catRequest);

        // 3. Validar roles y organización
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isRescatista = auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role -> role.equals("ROLE_RESCATISTA"));

        if (catRequest.getOrganizationId() != null) {
            Organization org = organizationRepository.findById(catRequest.getOrganizationId())
                    .orElseThrow(() -> new EntityNotFoundException("Organization not found with id " + catRequest.getOrganizationId()));
            cat.setOrganization(org);
        } else {
            if (!isRescatista) {
                throw new IllegalArgumentException("Solo usuarios con rol RESCATISTA pueden crear gatos sin organización");
            }
            cat.setOrganization(null);
        }

        cat.setStatus(Cat.CatStatus.AVAILABLE);

        // 4. Guardar gato
        Cat savedCat = catRepository.save(cat);

        // 5. Guardar características en Firebase
        if (catRequest.getFeatures() != null && !catRequest.getFeatures().isEmpty()) {
            try {
                catFeatureService.saveFeaturesForCat(
                        savedCat.getCatId(),
                        catRequest.getFeatures(),
                        catRequest.getCreatedBy().toString()
                );
            } catch (Exception e) {
                System.err.println("⚠️ Error guardando características en Firebase");
                e.printStackTrace();
            }
        }

        return mapToResponse(savedCat);
    }

    @Override
    public CatResponse updateCat(UUID catId, CatRequest catRequest) {
        Cat existingCat = catRepository.findById(catId)
                .orElseThrow(() -> new EntityNotFoundException("Cat not found with id " + catId));

        updateEntityFromRequest(existingCat, catRequest);
        Cat updatedCat = catRepository.save(existingCat);

        // Actualizar características si vienen (puede sobrescribir)
        if (catRequest.getFeatures() != null && !catRequest.getFeatures().isEmpty()) {
            try {
                catFeatureService.saveFeaturesForCat(
                        catId,
                        catRequest.getFeatures(),
                        catRequest.getCreatedBy().toString()
                );
            } catch (Exception e) {
                System.err.println("⚠️ Error actualizando características en Firebase");
                e.printStackTrace();
            }
        }

        return mapToResponse(updatedCat);
    }

    @Override
    public void deleteCat(UUID catId) {
        catRepository.deleteById(catId);
    }

    // 🔧 Actualización por referencia
    private void updateEntityFromRequest(Cat cat, CatRequest dto) {
        cat.setName(dto.getName());
        cat.setBirthDate(dto.getBirthDate());
        cat.setGender(dto.getGender());
        cat.setSize(dto.getSize());
        cat.setHealthStatus(dto.getHealthStatus());
        cat.setRaza(dto.getRaza());
        cat.setDescription(dto.getDescription());
        cat.setLocation(dto.getLocation());
        cat.setLatitude(dto.getLatitude());
        cat.setLongitude(dto.getLongitude());
        cat.setMainImageUrl(dto.getMainImageUrl());

        if (dto.getCreatedBy() != null) {
            User user = userRepository.findById(dto.getCreatedBy())
                    .orElseThrow(() -> new EntityNotFoundException("User not found with id " + dto.getCreatedBy()));
            cat.setCreatedBy(user);
        } else {
            throw new EntityNotFoundException("createdBy is required");
        }
    }

    // 🔁 Creación
    private Cat mapToEntity(CatRequest dto) {
        Cat cat = new Cat();
        updateEntityFromRequest(cat, dto);
        return cat;
    }

    // 🔁 Respuesta
    private CatResponse mapToResponse(Cat cat) {
        List<CatFeatureRequest> features = null;
        try {
            features = catFeatureService.getFeaturesByCatId(cat.getCatId()).stream()
                    .map(f -> CatFeatureRequest.builder()
                            .id(f.getId())
                            .name(f.getName())
                            .build())
                    .collect(Collectors.toList());
        } catch (Exception e) {
            System.err.println("⚠️ No se pudieron cargar las características de Firebase");
        }

        return CatResponse.builder()
                .catId(cat.getCatId())
                .name(cat.getName())
                .birthDate(cat.getBirthDate())
                .gender(cat.getGender())
                .size(cat.getSize())
                .healthStatus(cat.getHealthStatus())
                .raza(cat.getRaza())
                .description(cat.getDescription())
                .location(cat.getLocation())
                .latitude(cat.getLatitude())
                .longitude(cat.getLongitude())
                .status(cat.getStatus() != null ? cat.getStatus().name() : null)
                .publishedAt(cat.getPublishedAt())
                .mainImageUrl(cat.getMainImageUrl())
                .createdBy(mapUserToUserResponse(cat.getCreatedBy()))
                .organizationId(cat.getOrganization() != null ? cat.getOrganization().getOrganizationId() : null)
                .sentToOrg(cat.getSentToOrg() != null ? cat.getSentToOrg().getOrganizationId() : null)
                .adoptedBy(cat.getAdoptedBy() != null ? cat.getAdoptedBy().getUserId() : null)
                .adoptedAt(cat.getAdoptedAt())
                .adoptionRequestId(cat.getAdoptionRequest() != null ? cat.getAdoptionRequest().getRequestId() : null)
                .features(features)
                .build();
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
