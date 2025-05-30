package com.adocat.adocat_api.service.interfaces;

import com.adocat.adocat_api.api.dto.cat.CatRequest;
import com.adocat.adocat_api.api.dto.cat.CatResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface ICatService {

    List<CatResponse> getAllCats();
    List<CatResponse> getAllCatsByOrganization(UUID organizationId);

    String uploadCatPhoto(UUID catId, MultipartFile imageFile);

    CatResponse getCatById(UUID catId);

    CatResponse createCat(CatRequest catRequest, MultipartFile file);

    CatResponse updateCat(UUID catId, CatRequest catRequest, MultipartFile file);

    void deleteCat(UUID catId);
}
