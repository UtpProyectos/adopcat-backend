package com.adocat.adocat_api.api.dto.user;

import lombok.Data;

import java.util.UUID;
import lombok.Builder;

@Data
@Builder
public class UserResponse {
    private UUID userId;
    private String firstName;
    private String lastName;
    private String email;
    private String role;
    private String profilePhoto;
    private Boolean verified;
    private Boolean enabled;
    private String dni;
    private String phoneNumber;
    private String address;
    private Boolean emailVerified;
    private Boolean phoneVerified;
    private String dniUrl;
    private Boolean adminApproved;
}
