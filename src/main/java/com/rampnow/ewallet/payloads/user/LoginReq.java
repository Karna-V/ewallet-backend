package com.rampnow.ewallet.payloads.user;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginReq {
    @NotBlank(message = "username can't be null/blank")
    private String username;
    @NotBlank(message = "password can't be null/blank")
    private String password;
}
