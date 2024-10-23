package com.rampnow.ewallet.models.user;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Phone {
    @NotBlank(message = "countryCode can't be null/blank")
    private String countryCode;
    @NotBlank(message = "number can't be null/blank")
    private Integer number;
}
