package com.adocat.adocat_api.service.interfaces;

import com.adocat.adocat_api.api.dto.user.UserRequest;
import com.adocat.adocat_api.api.dto.user.UserResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface IUserService {
    UserResponse updateProfile(UUID userId, UserRequest request);
    UserResponse uploadDni(UUID userId, MultipartFile dniFile);
    UserResponse getCurrentUser();
    UserResponse getUserById(UUID userId);

    void changePassword(UUID userId, String currentPassword, String newPassword);
    void initiatePasswordReset(String email);
    void resetPassword(String token, String newPassword);

    void sendPhoneVerification(String phoneNumber);
    void verifyPhoneCode(UUID userId, String phoneNumber, String code);

    void sendEmailVerification(UUID userId);
    void confirmEmailVerification(UUID userId, String code) ;
    void updateUserApproval(UUID userId, boolean approve);

    List<UserResponse> getAllUsers();
}
