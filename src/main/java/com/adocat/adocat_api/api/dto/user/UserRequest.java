package com.adocat.adocat_api.api.dto.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserRequest {
    private String firstName;
    private String lastName;
    private String dni;
    private String phoneNumber;
    private String address;
}
