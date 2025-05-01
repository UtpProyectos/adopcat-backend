package com.adocat.adocat_api.service.interfaces;

import com.adocat.adocat_api.api.dto.user.UserRequest;
import com.adocat.adocat_api.api.dto.user.UserResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public interface IUserService {
    UserResponse updateProfile(UUID userId, UserRequest request);
    UserResponse uploadDni(UUID userId, MultipartFile dniFile);
    UserResponse getCurrentUser();
    UserResponse getUserById(UUID userId);

}
