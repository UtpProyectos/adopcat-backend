package com.adocat.adocat_api.api.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmailVerificationRequest {
    private String code;
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
}
