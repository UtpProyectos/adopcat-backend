package com.adocat.adocat_api.api.dto.adoption;

import com.adocat.adocat_api.api.dto.cat.CatResponse;
import com.adocat.adocat_api.api.dto.user.UserResponse;
import com.adocat.adocat_api.domain.entity.Adoption;
import com.adocat.adocat_api.domain.entity.Cat;
import lombok.*;

import java.sql.Timestamp;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdoptionResponse {
    private UUID requestId;
    private String status;
    private boolean finalized;
    private Timestamp submittedAt;
    private Timestamp decisionDate;
    private Adoption.InterviewType interviewType;
    private CatResponse cat;
    private UserResponse adopter;

    private String reason;
    private String experience;
    private String residenceType;
    private String reactionPlan;
    private Boolean followUpConsent;

    private String receiptUrl;
    private String homePhotoUrl;
    private String commitmentUrl;

}