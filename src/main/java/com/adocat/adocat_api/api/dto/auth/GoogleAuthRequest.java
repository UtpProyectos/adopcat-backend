package com.adocat.adocat_api.api.dto.auth;

import lombok.Data;

@Data
public class GoogleAuthRequest {
    private String idToken;
}