package com.adocat.adocat_api.service.interfaces;

import com.adocat.adocat_api.api.dto.cat.CatRequest;
import com.adocat.adocat_api.api.dto.cat.CatResponse;

import java.util.List;
import java.util.UUID;

public interface ICatService {

    List<CatResponse> getAllCats();

    CatResponse getCatById(UUID catId);

    CatResponse createCat(CatRequest catRequest);

    CatResponse updateCat(UUID catId, CatRequest catRequest);

    void deleteCat(UUID catId);
}
