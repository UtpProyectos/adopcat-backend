package com.adocat.adocat_api.service.interfaces;

import com.adocat.adocat_api.api.dto.cat.CatRequest;
import com.adocat.adocat_api.api.dto.cat.CatResponse;

import java.util.List;

public interface ICatService {
    CatResponse createCat(CatRequest request);
    List<CatResponse> getAllCats();
}
