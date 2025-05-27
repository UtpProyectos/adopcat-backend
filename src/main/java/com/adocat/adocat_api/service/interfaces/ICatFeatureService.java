package com.adocat.adocat_api.service.interfaces;

import com.adocat.adocat_api.api.dto.cat.CatFeatureRequest;
import com.adocat.adocat_api.api.dto.cat.CatFeatureResponse;

import java.util.List;
import java.util.UUID;

public interface ICatFeatureService {

    void saveFeaturesForCat(UUID catId, List<CatFeatureRequest> features, String createdBy) throws Exception;

    List<CatFeatureResponse> getFeaturesByCatId(UUID catId) throws Exception;

    List<CatFeatureResponse> getAllFeatures() throws Exception;

}
