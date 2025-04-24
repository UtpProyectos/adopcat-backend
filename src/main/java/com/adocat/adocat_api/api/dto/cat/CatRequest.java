package com.adocat.adocat_api.api.dto.cat;

import lombok.Data;

@Data
public class CatRequest {
    private String name;
    private String age;
    private String gender;
}