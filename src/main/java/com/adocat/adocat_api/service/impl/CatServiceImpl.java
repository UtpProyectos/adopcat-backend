package com.adocat.adocat_api.service.impl;


import com.adocat.adocat_api.api.dto.cat.CatRequest;
import com.adocat.adocat_api.api.dto.cat.CatResponse;
import com.adocat.adocat_api.api.dto.user.UserResponse;
import com.adocat.adocat_api.config.CloudinaryService;
import com.adocat.adocat_api.domain.entity.Cat;
import com.adocat.adocat_api.domain.entity.User;
import com.adocat.adocat_api.domain.entity.Organization;
import com.adocat.adocat_api.domain.entity.AdoptionRequest;
import com.adocat.adocat_api.domain.repository.AdoptionRequestRepository;
import com.adocat.adocat_api.domain.repository.CatRepository;
import com.adocat.adocat_api.domain.repository.UserRepository;
import com.adocat.adocat_api.domain.repository.OrganizationRepository;
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

        System.out.println(file);
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

        // 5. Devolver respuesta
        return mapToResponse(savedCat);
    }



    @Override
    public CatResponse updateCat(UUID catId, CatRequest catRequest) {
        Cat existingCat = catRepository.findById(catId)
                .orElseThrow(() -> new EntityNotFoundException("Cat not found with id " + catId));

        // Actualizar SOLO los campos que mencionaste para creación/actualización principal
        existingCat.setName(catRequest.getName());
        existingCat.setBirthDate(catRequest.getBirthDate());
        existingCat.setGender(catRequest.getGender());
        existingCat.setSize(catRequest.getSize());
        existingCat.setHealthStatus(catRequest.getHealthStatus());
        existingCat.setRaza(catRequest.getRaza());
        existingCat.setDescription(catRequest.getDescription());
        existingCat.setLocation(catRequest.getLocation());
        existingCat.setLatitude(catRequest.getLatitude());
        existingCat.setLongitude(catRequest.getLongitude());

        // Relaciones que se mantienen necesarias para la entidad (ej. createdBy)
        if (catRequest.getCreatedBy() != null) {
            User user = userRepository.findById(catRequest.getCreatedBy())
                    .orElseThrow(() -> new EntityNotFoundException("User not found with id " + catRequest.getCreatedBy()));
            existingCat.setCreatedBy(user);
        } else {
            throw new EntityNotFoundException("createdBy is required");
        }
        Cat updatedCat = catRepository.save(existingCat);
        return mapToResponse(updatedCat);
    }


    @Override
    public void deleteCat(UUID catId) {
        catRepository.deleteById(catId);
    }

    // Mappers manuales

    private CatResponse mapToResponse(Cat cat) {
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
                .createdBy(mapUserToUserResponse(cat.getCreatedBy()) )
                .organizationId(cat.getOrganization() != null ? cat.getOrganization().getOrganizationId() : null)
                .sentToOrg(cat.getSentToOrg() != null ? cat.getSentToOrg().getOrganizationId() : null)
                .adoptedBy(cat.getAdoptedBy() != null ? cat.getAdoptedBy().getUserId() : null)
                .adoptedAt(cat.getAdoptedAt())
                .adoptionRequestId(cat.getAdoptionRequest() != null ? cat.getAdoptionRequest().getRequestId() : null)
                .build();
    }

    private Cat mapToEntity(CatRequest dto) {
        Cat cat = new Cat();
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

        cat.setMainImageUrl(dto.getMainImageUrl());  // <--- importante!

        if (dto.getCreatedBy() != null) {
            User user = userRepository.findById(dto.getCreatedBy())
                    .orElseThrow(() -> new EntityNotFoundException("User not found with id " + dto.getCreatedBy()));
            cat.setCreatedBy(user);
        } else {
            throw new EntityNotFoundException("createdBy is required");
        }
        return cat;
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
