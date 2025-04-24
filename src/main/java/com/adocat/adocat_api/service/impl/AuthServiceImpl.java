package com.adocat.adocat_api.service.impl;

import com.adocat.adocat_api.api.dto.auth.*;
import com.adocat.adocat_api.domain.entity.Role;
import com.adocat.adocat_api.domain.entity.User;
import com.adocat.adocat_api.domain.repository.RoleRepository;
import com.adocat.adocat_api.domain.repository.UserRepository;
import com.adocat.adocat_api.security.GoogleTokenVerifier;
import com.adocat.adocat_api.security.JwtService;
import com.adocat.adocat_api.service.interfaces.IAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements IAuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final JwtService jwtService;
    private final GoogleTokenVerifier googleVerifier;
    private final BCryptPasswordEncoder encoder;

    @Override
    public TokenResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (!encoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new RuntimeException("Credenciales incorrectas");
        }

        String token = jwtService.generateToken(user);
        return new TokenResponse(token);
    }

    @Override
    public TokenResponse authenticateWithGoogle(String idToken) {
        GoogleUser googleUser = googleVerifier.verify(idToken);

        User user = userRepository.findByEmail(googleUser.getEmail())
                .orElseGet(() -> {
                    Role role = roleRepository.findByRoleName("ROLE_ADOPTANTE")
                            .orElseThrow(() -> new RuntimeException("Rol ROLE_ADOPTANTE no encontrado"));

                    User newUser = User.builder()
                            .email(googleUser.getEmail())
                            .firstName(googleUser.getGivenName())
                            .lastName(googleUser.getFamilyName())
                            .enabled(true)
                            .verified(true)
                            .adminApproved(true)
                            .emailVerified(true)
                            .phoneVerified(false)
                            .role(role)
                            .createdAt(LocalDateTime.now())
                            .build();

                    return userRepository.save(newUser);
                });

        String token = jwtService.generateToken(user);
        return new TokenResponse(token);
    }

    @Override
    public TokenResponse register(RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("El email ya está registrado.");
        }

        Role defaultRole = roleRepository.findByRoleName("ROLE_ADOPTANTE")
                .orElseThrow(() -> new RuntimeException("Rol ROLE_ADOPTANTE no encontrado"));

        User newUser = User.builder()
                .email(request.getEmail())
                .passwordHash(encoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .enabled(true)
                .verified(false)
                .adminApproved(false)
                .emailVerified(false)
                .phoneVerified(false)
                .role(defaultRole)
                .createdAt(LocalDateTime.now())
                .build();

        userRepository.save(newUser);

        String token = jwtService.generateToken(newUser);
        return new TokenResponse(token);
    }

}
