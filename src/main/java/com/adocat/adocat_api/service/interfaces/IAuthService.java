package com.adocat.adocat_api.service.interfaces;

import com.adocat.adocat_api.api.dto.auth.*;

public interface IAuthService {
    TokenResponse login(LoginRequest request);
    TokenResponse authenticateWithGoogle(String idToken);
    TokenResponse register(RegisterRequest request);

}
