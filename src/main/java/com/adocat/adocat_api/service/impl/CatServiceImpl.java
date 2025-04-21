package com.adocat.adocat_api.service.impl;

import com.adocat.adocat_api.api.dto.request.CatRequest;
import com.adocat.adocat_api.api.dto.response.CatResponse;
import com.adocat.adocat_api.domain.entity.Cat;
import com.adocat.adocat_api.domain.repository.CatRepository;
import com.adocat.adocat_api.service.interfaces.ICatService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CatServiceImpl implements ICatService {

    private final CatRepository catRepository;

    @Override
    public CatResponse createCat(CatRequest request) {
        Cat cat = new Cat();
        cat.setName(request.getName());
        cat.setAge(request.getAge());
        cat.setGender(request.getGender());
        cat.setImageUrl(null); // por ahora, sin imagen

        Cat saved = catRepository.save(cat);

        return toResponse(saved);
    }

    @Override
    public List<CatResponse> getAllCats() {
        return catRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private CatResponse toResponse(Cat cat) {
        CatResponse response = new CatResponse();
        response.setId(cat.getId());
        response.setName(cat.getName());
        response.setAge(cat.getAge());
        response.setGender(cat.getGender());
        response.setImageUrl(cat.getImageUrl());
        return response;
    }
}
