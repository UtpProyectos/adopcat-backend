package com.adocat.adocat_api.api.controller;

import com.adocat.adocat_api.api.dto.cat.CatFeatureResponse;
import com.adocat.adocat_api.api.dto.cat.CatRequest;
import com.adocat.adocat_api.api.dto.cat.CatResponse;
import com.adocat.adocat_api.service.impl.CatFeatureServiceImpl;
import com.adocat.adocat_api.service.interfaces.ICatFeatureService;
import com.adocat.adocat_api.service.interfaces.ICatService;
import jakarta.annotation.security.RolesAllowed;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/cats")
@RequiredArgsConstructor
public class CatController {

    private final ICatService catService;
    private final ICatFeatureService catFeatureService;

    /**
     * Crea un gato. Si no viene organizationId, se valida rol ROLE_RESCATISTA en el servicio.
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public CatResponse createCat(
            @RequestPart("cat") CatRequest catRequest,
            @RequestPart(value = "file", required = false) MultipartFile file) {
        System.out.println("File: "+ file);
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

    @PutMapping("/{id}")
    public CatResponse updateCat(@PathVariable UUID id, @RequestBody CatRequest request) {
        return catService.updateCat(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCat(@PathVariable UUID id) {
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


}
