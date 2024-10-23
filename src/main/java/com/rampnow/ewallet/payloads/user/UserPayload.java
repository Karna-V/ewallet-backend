package com.rampnow.ewallet.payloads.user;

import com.rampnow.ewallet.models.user.Phone;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserPayload {
    @NotBlank(message = "name can't be null/blank")
    private String name;
    @NotBlank(message = "username can't be null/blank")
    private String username;
    @NotBlank(message = "emailId can't be null/blank")
    private String emailId;
    @NotNull(message = "phone can't be null")
    private Phone phone;
}
