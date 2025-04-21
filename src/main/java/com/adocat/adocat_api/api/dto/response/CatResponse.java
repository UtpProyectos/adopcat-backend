package com.adocat.adocat_api.api.dto.response;

import lombok.Data;

@Data
public class CatResponse {
    private String id;
    private String name;
    private String age;
    private String gender;
    private String imageUrl;
}
