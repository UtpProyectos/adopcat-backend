package com.adocat.adocat_api.api.controller;

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


}
