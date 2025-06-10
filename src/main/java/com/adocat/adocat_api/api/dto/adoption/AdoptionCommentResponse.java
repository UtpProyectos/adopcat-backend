package com.adocat.adocat_api.api.dto.adoption;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdoptionCommentResponse {
    private String comment;
    private String userName;
    private String profilePhoto;
    private Instant createdAt;
}
