package com.adocat.adocat_api.api.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PhoneVerificationRequest {
    private String phoneNumber;
    private String code; // puede venir vacío o null si solo se quiere enviar el SMS
}
