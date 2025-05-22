package com.adocat.adocat_api.api.controller;

import com.adocat.adocat_api.api.dto.cat.CatRequest;
import com.adocat.adocat_api.api.dto.cat.CatResponse;
import com.adocat.adocat_api.service.interfaces.ICatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/cats")
@RequiredArgsConstructor
public class CatController {

    private final ICatService catService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CatResponse createCat(@RequestBody CatRequest request) {
        return catService.createCat(request);
    }

    @GetMapping
    public List<CatResponse> getAllCats() {
        return catService.getAllCats();
    }

    @GetMapping("/{id}")
    public CatResponse getCatById(@PathVariable("id") UUID id) {
        return catService.getCatById(id);
    }

    @PutMapping("/{id}")
    public CatResponse updateCat(@PathVariable("id") UUID id, @RequestBody CatRequest request) {
        return catService.updateCat(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCat(@PathVariable("id") UUID id) {
        catService.deleteCat(id);
    }
}
