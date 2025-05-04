package com.adocat.adocat_api.service.impl;

import com.adocat.adocat_api.api.dto.auth.*;
import com.adocat.adocat_api.api.dto.user.UserResponse;
import com.adocat.adocat_api.config.MailService;
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
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements IAuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final JwtService jwtService;
    private final GoogleTokenVerifier googleVerifier;
    private final BCryptPasswordEncoder encoder;
    private final MailService mailService;


    @Override
    public TokenResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (!encoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new RuntimeException("Credenciales incorrectas");
        }

        String token = jwtService.generateToken(user);
        UserResponse userResponse = UserResponse.builder()
                .userId(user.getUserId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .role(user.getRole().getRoleName())
                .profilePhoto(user.getProfilePhoto())
                .verified(user.getVerified())
                .build();


        return new TokenResponse(token, userResponse);
    }

//    @Transactional
//    @Override
//    public TokenResponse authenticateWithGoogle(String idToken) {
//        GoogleUser googleUser = googleVerifier.verify(idToken);
//
//        User user = userRepository.findByEmail(googleUser.getEmail())
//                .orElseGet(() -> {
//                    Role role = roleRepository.findByRoleName("ROLE_ADOPTANTE")
//                            .orElseThrow(() -> new RuntimeException("Rol ROLE_ADOPTANTE no encontrado"));
//
//                    User newUser = User.builder()
//                            .email(googleUser.getEmail())
//                            .firstName(googleUser.getFirstName())
//                            .lastName(googleUser.getLastName())
//                            .enabled(true)
//                            .verified(true)
//                            .adminApproved(false)
//                            .emailVerified(true)
//                            .phoneVerified(false)
//                            .role(role)
//                            .createdAt(LocalDateTime.now())
//                            .build();
//
//                    newUser.setPasswordHash(encoder.encode("dummy-google-pass"));
//                    newUser.setProfilePhoto(googleUser.getPictureUrl());
//                    return userRepository.saveAndFlush(newUser);
//                });
//
//        String token = jwtService.generateToken(user);
//
//        UserResponse userResponse = UserResponse.builder()
//                .userId(user.getUserId())
//                .firstName(user.getFirstName())
//                .lastName(user.getLastName())
//                .email(user.getEmail())
//                .role(user.getRole().getRoleName())
//                .profilePhoto(user.getProfilePhoto())
//                .verified(user.getVerified())
//                .build();
//
//
//        return new TokenResponse(token, userResponse);
//
//    }

    @Transactional
    @Override
    public TokenResponse authenticateWithGoogle(String idToken) {
        GoogleUser googleUser = googleVerifier.verify(idToken);

        // 🔍 Verifica si ya existe
        User user = userRepository.findByEmail(googleUser.getEmail()).orElse(null);
        boolean isNewUser = false;

        if (user == null) {
            System.out.println("Usuarios nuevo");
            Role role = roleRepository.findByRoleName("ROLE_ADOPTANTE")
                    .orElseThrow(() -> new RuntimeException("Rol ROLE_ADOPTANTE no encontrado"));

            user = User.builder()
                    .email(googleUser.getEmail())
                    .firstName(googleUser.getFirstName())
                    .lastName(googleUser.getLastName())
                    .enabled(true)
                    .verified(true)
                    .adminApproved(false)
                    .emailVerified(true)
                    .phoneVerified(false)
                    .role(role)
                    .createdAt(LocalDateTime.now())
                    .build();

            user.setPasswordHash(encoder.encode("dummy-google-pass"));
            user.setProfilePhoto(googleUser.getPictureUrl());

            user = userRepository.saveAndFlush(user);
            isNewUser = true;
        }

        // ✅ Solo si es nuevo enviamos correo de bienvenida
        if (isNewUser) {
            mailService.sendHtmlEmail(
                    user.getEmail(),
                    "¡Bienvenido a AdoptaCat con Google! 🐱",
                    "welcome",
                    Map.of("name", user.getFirstName())
            );
        }

        String token = jwtService.generateToken(user);

        UserResponse userResponse = UserResponse.builder()
                .userId(user.getUserId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .role(user.getRole().getRoleName())
                .profilePhoto(user.getProfilePhoto())
                .verified(user.getVerified())
                .build();

        return new TokenResponse(token, userResponse);
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

        UserResponse userResponse = UserResponse.builder()
                .userId(newUser.getUserId())
                .firstName(newUser.getFirstName())
                .lastName(newUser.getLastName())
                .email(newUser.getEmail())
                .role(newUser.getRole().getRoleName())
                .profilePhoto(newUser.getProfilePhoto())
                .verified(newUser.getVerified())
                .build();

        mailService.sendHtmlEmail(
                newUser.getEmail(),
                "¡Bienvenido a AdoptaCat! 🐾",
                "welcome-email",  // nombre del template
                Map.of(
                        "name", newUser.getFirstName()
                )
        );

        return new TokenResponse(token, userResponse);
    }

}
