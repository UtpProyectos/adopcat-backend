package com.adocat.adocat_api.api.controller;

import com.adocat.adocat_api.api.dto.user.EmailVerificationRequest;
import com.adocat.adocat_api.api.dto.user.PhoneVerificationRequest;
import com.adocat.adocat_api.api.dto.user.UserRequest;
import com.adocat.adocat_api.api.dto.user.UserResponse;
import com.adocat.adocat_api.service.interfaces.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final IUserService userService;

    /**
     * 🔁 Actualizar perfil del usuario (datos personales).
     */
    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateProfile(
            @PathVariable UUID id,
            @RequestBody UserRequest request
    ) {
        UserResponse response = userService.updateProfile(id, request);
        return ResponseEntity.ok(response);
    }

    /**
     * 📤 Subir documento DNI del usuario.
     */
    @PostMapping("/{id}/dni")
    public ResponseEntity<UserResponse> uploadDni(
            @PathVariable UUID id,
            @RequestParam("file") MultipartFile file
    ) {
        UserResponse response = userService.uploadDni(id, file);
        return ResponseEntity.ok(response);
    }


    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser() {
        return ResponseEntity.ok(userService.getCurrentUser());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable UUID id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @PutMapping("/{id}/change-password")
    @PreAuthorize("#id == principal.userId")
    public ResponseEntity<Void> changePassword(
            @PathVariable UUID id,
            @RequestParam String currentPassword,
            @RequestParam String newPassword
    ) {

        userService.changePassword(id, currentPassword, newPassword);
        return ResponseEntity.ok().build();
    }



    //VALIDACION DE NUMERO
    @PostMapping("/send-phone-verification")
    public ResponseEntity<Void> sendPhoneVerification(@RequestBody PhoneVerificationRequest request) {
        userService.sendPhoneVerification(request.getPhoneNumber());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/verify-phone")
    public ResponseEntity<String> verifyPhone(
            @PathVariable UUID id,
            @RequestBody PhoneVerificationRequest request
    ) {
        userService.verifyPhoneCode(id, request.getPhoneNumber(), request.getCode());
        return ResponseEntity.ok("Teléfono verificado correctamente");
    }


    //VALIDACION CORREO
    @PostMapping("/{id}/verify-email/send")
        public ResponseEntity<Void> sendEmailVerification(@PathVariable UUID id) {
        userService.sendEmailVerification(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/verify-email/confirm")
    public ResponseEntity<String> confirmEmailVerification(
            @PathVariable UUID id,
            @RequestBody EmailVerificationRequest request) {
        userService.confirmEmailVerification(id, request.getCode());
        return ResponseEntity.ok("Email verificado correctamente");
    }


}
