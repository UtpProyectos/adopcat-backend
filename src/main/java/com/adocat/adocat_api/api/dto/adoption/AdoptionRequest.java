package com.adocat.adocat_api.api.dto.adoption;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AdoptionRequest {
    private String reason;
    private String experience;
    private String residenceType;
    private String reactionPlan;
    private String followUpConsent;
}
