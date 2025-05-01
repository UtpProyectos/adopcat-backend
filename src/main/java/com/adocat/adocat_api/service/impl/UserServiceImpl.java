package com.adocat.adocat_api.service.impl;

import com.adocat.adocat_api.api.dto.user.UserRequest;
import com.adocat.adocat_api.api.dto.user.UserResponse;
import com.adocat.adocat_api.domain.entity.User;
import com.adocat.adocat_api.domain.repository.UserRepository;
import com.adocat.adocat_api.service.CloudinaryService;
import com.adocat.adocat_api.service.interfaces.IUserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements IUserService {

    private final UserRepository userRepository;
    private final CloudinaryService cloudinaryService; // ✅ CAMBIO: ahora usamos CloudinaryService directamente

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
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

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

}
