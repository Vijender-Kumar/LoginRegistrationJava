package com.vijender.RegisterLogin.dto.LoginDto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginDtoRequest {
    private String email;
    private String password;
}
