package com.adocat.adocat_api.api.controller;

import com.adocat.adocat_api.api.dto.cat.CatFeatureResponse;
import com.adocat.adocat_api.api.dto.cat.CatRequest;
import com.adocat.adocat_api.api.dto.cat.CatResponse;
import com.adocat.adocat_api.domain.entity.Cat;
import com.adocat.adocat_api.domain.entity.User;
import com.adocat.adocat_api.domain.repository.CatRepository;
import com.adocat.adocat_api.security.OrganizationAccessService;
import com.adocat.adocat_api.service.impl.CatFeatureServiceImpl;
import com.adocat.adocat_api.service.interfaces.ICatFeatureService;
import com.adocat.adocat_api.service.interfaces.ICatService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/cats")
@RequiredArgsConstructor
public class CatController {

    private final ICatService catService;
    private final ICatFeatureService catFeatureService;
    private final OrganizationAccessService organizationAccessService;
    private final CatRepository catRepository;

    /**
     * Crea un gato. Si no viene organizationId, se valida rol ROLE_RESCATISTA en el servicio.
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN') or hasRole('RESCATISTA')")
    public CatResponse createCat(
            @RequestPart("cat") CatRequest catRequest,
            @RequestPart(value = "file", required = false) MultipartFile file) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) auth.getPrincipal();

        UUID orgId = catRequest.getOrganizationId();
        organizationAccessService.isMemberOrOwner(orgId, user);

        return catService.createCat(catRequest, file);
    }


    /**
     * Obtiene todos los gatos. Si se pasa organizationId, filtra por organización.
     */
    @GetMapping("/organization")
    public List<CatResponse> getAllCatsOrganization(@RequestParam(required = false) UUID organizationId) {
        if (organizationId != null) {
            return catService.getAllCatsByOrganization(organizationId);
        }
        return catService.getAllCats();
    }

    @GetMapping
    public List<CatResponse> getAllCats() {
        return catService.getAllCats();
    }

    @GetMapping("/{id}")
    public CatResponse getCatById(@PathVariable UUID id) {
        return catService.getCatById(id);
    }


    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN') or hasRole('RESCATISTA')")
    public CatResponse updateCat(
            @PathVariable UUID id,
            @RequestPart("cat") CatRequest request,
            @RequestPart(value = "file", required = false) MultipartFile file) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) auth.getPrincipal();
        Cat cat = catRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Gato no encontrado"));
        organizationAccessService.isMemberOrOwner(cat.getOrganization().getOrganizationId(), user);

        return catService.updateCat(id, request, file);
    }


    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN') or hasRole('RESCATISTA')")
    public void deleteCat(@PathVariable UUID id) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) auth.getPrincipal();
        Cat cat = catRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Gato no encontrado"));
        organizationAccessService.isMemberOrOwner(cat.getOrganization().getOrganizationId(), user);
        catService.deleteCat(id);
    }

    @GetMapping("/{id}/features")
    public List<com.adocat.adocat_api.api.dto.cat.CatFeatureResponse> getFeaturesByCat(@PathVariable UUID id) throws Exception {
        return catFeatureService.getFeaturesByCatId(id);
    }

    @GetMapping("/features")
    public List<CatFeatureResponse> getAllFeatures() throws Exception {
        return catFeatureService.getAllFeatures();
    }

    @PostMapping("/{id}/photos")
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, String> uploadCatPhoto(
            @PathVariable UUID id,
            @RequestPart("file") MultipartFile file) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) auth.getPrincipal();
        Cat cat = catRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Gato no encontrado"));
        organizationAccessService.isMemberOrOwner(cat.getOrganization().getOrganizationId(), user);

        String uploadedUrl = catService.uploadCatPhoto(id, file);
        return Collections.singletonMap("url", uploadedUrl);
    }

}
