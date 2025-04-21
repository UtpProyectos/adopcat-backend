package com.adocat.adocat_api.service.interfaces;

import com.adocat.adocat_api.api.dto.request.CatRequest;
import com.adocat.adocat_api.api.dto.response.CatResponse;

import java.util.List;

public interface ICatService {
    CatResponse createCat(CatRequest request);
    List<CatResponse> getAllCats();
}
