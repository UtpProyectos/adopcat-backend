package com.adocat.adocat_api.api.dto.cat;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CatFeatureRequest {
    private String id;    // ID personalizado tipo "jugueton"
    private String name;  // Nombre visible "Juguetón"
}
