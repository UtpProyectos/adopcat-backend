package com.adocat.adocat_api.api.dto.cat;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CatPhotoResponse {
    private String id;
    private String url;
    private String createdBy;
    private String createdAt;
}
