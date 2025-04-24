package com.adocat.adocat_api.api.controller;

import com.adocat.adocat_api.api.dto.cat.CatRequest;
import com.adocat.adocat_api.api.dto.cat.CatResponse;
import com.adocat.adocat_api.service.interfaces.ICatService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/cats")
@RequiredArgsConstructor
public class CatController {

    private final ICatService catService;

    @PostMapping
    public CatResponse createCat(@RequestBody CatRequest request) {

        return catService.createCat(request);
    }

    @GetMapping()
    public List<CatResponse> getAllCats() {
        return catService.getAllCats();
    }


}
