package com.adocat.adocat_api.api.dto.auth;
import com.adocat.adocat_api.api.dto.user.UserResponse;
import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
public class TokenResponse {
    private String token;
    private UserResponse user;
}