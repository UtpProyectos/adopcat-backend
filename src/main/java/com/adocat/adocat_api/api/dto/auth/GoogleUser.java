package com.adocat.adocat_api.api.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GoogleUser {
    private String email;
    private String givenName;
    private String familyName;
}