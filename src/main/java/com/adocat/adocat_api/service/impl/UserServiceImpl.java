package com.adocat.adocat_api.service.impl;

import com.adocat.adocat_api.api.dto.user.UserRequest;
import com.adocat.adocat_api.api.dto.user.UserResponse;
import com.adocat.adocat_api.config.MailService;
import com.adocat.adocat_api.config.TwilioService;
import com.adocat.adocat_api.domain.entity.PasswordResetToken;
import com.adocat.adocat_api.domain.entity.User;
import com.adocat.adocat_api.domain.repository.PasswordResetTokenRepository;
import com.adocat.adocat_api.domain.repository.UserRepository;
import com.adocat.adocat_api.service.CloudinaryService;
import com.adocat.adocat_api.service.interfaces.IUserService;
import com.google.api.gax.rpc.NotFoundException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements IUserService {

    @Value("${vite.url}")
    private String viteUrl;
    private final UserRepository userRepository;
    private final CloudinaryService cloudinaryService;
    private final PasswordResetTokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final MailService mailService;
    private final TwilioService twilioService;

    @Override
    public UserResponse updateProfile(UUID userId, UserRequest request) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));

            if (request.getFirstName() != null) user.setFirstName(request.getFirstName());
            if (request.getLastName() != null) user.setLastName(request.getLastName());
            if (request.getDni() != null) {
                if (request.getDni().length() > 8) {
                    throw new IllegalArgumentException("El DNI no puede tener más de 8 caracteres");
                }
                user.setDni(request.getDni());
            }
            if (request.getPhoneNumber() != null) user.setPhoneNumber(request.getPhoneNumber());
            if (request.getAddress() != null) user.setAddress(request.getAddress());

            return toResponse(userRepository.save(user));
        } catch (EntityNotFoundException | IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error al actualizar el perfil del usuario: " + e.getMessage(), e);
        }
    }


    @Override
    public UserResponse uploadDni(UUID userId, MultipartFile dniFile) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));

            if (user.getDniUrl() != null) {
                cloudinaryService.deleteByUrl(user.getDniUrl());
            }

            String uploadedUrl = cloudinaryService.uploadFile(dniFile, "users/" + userId);
            user.setDniUrl(uploadedUrl);

            return toResponse(userRepository.save(user));
        } catch (EntityNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error al subir DNI: " + e.getMessage(), e);
        }
    }
    private UserResponse toResponse(User user) {
        return UserResponse.builder()
                .userId(user.getUserId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .role(user.getRole().getRoleName())
                .profilePhoto(user.getProfilePhoto())
                .verified(user.getVerified())
                .dni(user.getDni())
                .phoneNumber(user.getPhoneNumber())
                .address(user.getAddress())
                .emailVerified(user.getEmailVerified())
                .phoneVerified(user.getPhoneVerified())
                .dniUrl(user.getDniUrl())
                .adminApproved(user.getAdminApproved())
                .build();
    }


    @Override
    public UserResponse getCurrentUser() {
        User user1 = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        String email = user1.getEmail();

        User user = userRepository.findByEmailWithRole(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        return toResponse(user);
    }


    @Override
    public UserResponse getUserById(UUID userId) {
        User user = userRepository.findByIdWithRole(userId)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));

        return toResponse(user);
    }


    @Override
    public void changePassword(UUID userId, String currentPassword, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));

        if (!passwordEncoder.matches(currentPassword, user.getPasswordHash())) {
            throw new IllegalArgumentException("La contraseña actual es incorrecta");
        }

        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    @Override
    public void initiatePasswordReset(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Correo no registrado"));

        String token = UUID.randomUUID().toString();
        PasswordResetToken resetToken = PasswordResetToken.builder()
                .token(token)
                .userId(user.getUserId())
                .expiration(LocalDateTime.now().plusMinutes(30))
                .used(false)
                .build();

        tokenRepository.save(resetToken);

        mailService.sendHtmlEmail(
                user.getEmail(),
                "Restablecer contraseña - AdoCat",
                "reset-password",
                Map.of(
                        "name", user.getFirstName() + " " + user.getLastName(),
                        "resetLink", viteUrl + "/reset-password?token=" + token
                )
        );

    }

    @Override
    @Transactional
    public void resetPassword(String token, String newPassword) {
        PasswordResetToken resetToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new EntityNotFoundException("Token invalido"));

        if (resetToken.getExpiration().isBefore(LocalDateTime.now())) {
            tokenRepository.delete(resetToken);
            throw new IllegalArgumentException("El token expiró.");
        }


        User user = userRepository.findById(resetToken.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));

        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        resetToken.setUsed(true);
        tokenRepository.save(resetToken);
    }


    //Verificacion de numero
    @Override
    public void sendPhoneVerification(String phoneNumber) {
        twilioService.sendVerificationCode(phoneNumber);
    }

    @Override
    @Transactional
    public void verifyPhoneCode(UUID userId, String phoneNumber, String code) {
        boolean isVerified = twilioService.verifyCode(phoneNumber, code);

        if (!isVerified) {
            throw new IllegalArgumentException("Código incorrecto o expirado.");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));

        user.setPhoneVerified(true);
        userRepository.save(user);
    }

    //VERIFICACION MAIL
    @Override
    @Transactional
    public void sendEmailVerification(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));

        String code = String.format("%06d", new Random().nextInt(999999));
        user.setEmailVerificationCode(code);
        userRepository.save(user);

        mailService.sendHtmlEmail(
                user.getEmail(),
                "Verifica tu correo - AdoCat",
                "verify-email",
                Map.of("name", user.getFirstName(), "code", code)
        );
    }

    @Override
    @Transactional
    public void confirmEmailVerification(UUID userId, String code) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));

        if (!code.equals(user.getEmailVerificationCode())) {
            throw new IllegalArgumentException("Código inválido");
        }
        user.setEmailVerified(true);
        user.setEmailVerificationCode(null);
        userRepository.save(user);
    }


    @Override
    public void updateUserApproval(UUID userId, boolean approve) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));

        if (approve) {
            user.setAdminApproved(true); // Aprobar la verificación
            user.setVerified(true); // Confirmar que está verificado
            //sendApprovalNotification(user); // Enviar notificación de aprobación
        } else {
            user.setAdminApproved(false); // Rechazar la verificación
            user.setVerified(false); // Marcar como no verificado
            //sendRejectionNotification(user); // Enviar notificación de rechazo
        }

        userRepository.save(user);
    }

    @Override
    public List<UserResponse> getAllUsers() {
        List<User> users = userRepository.findAll();  // Consulta a la base de datos para obtener todos los usuarios
        return users.stream()
                .map(user -> UserResponse.builder()  // Creamos el UserResponse solo con los campos necesarios
                        .userId(user.getUserId())
                        .email(user.getEmail())
                        .emailVerified(user.getEmailVerified())
                        .dniUrl(user.getDniUrl())
                        .adminApproved(user.getAdminApproved())
                        .build())
                .collect(Collectors.toList());
    }


}
