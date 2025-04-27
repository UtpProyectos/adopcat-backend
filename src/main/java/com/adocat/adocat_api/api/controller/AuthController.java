package com.adocat.adocat_api.api.controller;

import com.adocat.adocat_api.api.dto.auth.*;
import com.adocat.adocat_api.domain.entity.Role;
import com.adocat.adocat_api.domain.entity.User;
import com.adocat.adocat_api.domain.repository.RoleRepository;
import com.adocat.adocat_api.domain.repository.UserRepository;
import com.adocat.adocat_api.security.GoogleTokenVerifier;
import com.adocat.adocat_api.security.JwtService;
import com.adocat.adocat_api.service.interfaces.IAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final IAuthService authService;
    private final GoogleTokenVerifier googleTokenVerifier;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final JwtService jwtService;

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/register")
    public ResponseEntity<TokenResponse> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/google")
    public ResponseEntity<TokenResponse> loginWithGoogle(@RequestBody GoogleAuthRequest request) {
        return ResponseEntity.ok(authService.authenticateWithGoogle(request.getIdToken()));
    }

}
