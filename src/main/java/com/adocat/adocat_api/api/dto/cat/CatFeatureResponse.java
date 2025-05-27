package com.adocat.adocat_api.api.dto.cat;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CatFeatureResponse {
    private String id;
    private String name;
    private String createdBy;
    private String createdAt;
}
