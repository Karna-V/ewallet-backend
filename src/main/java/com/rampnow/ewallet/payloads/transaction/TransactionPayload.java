package com.rampnow.ewallet.payloads.transaction;

import com.rampnow.ewallet.models.transaction.PaymentType;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionPayload {
    @NotBlank(message = "senderId can't be null/blank")
    private String senderId;
    @NotBlank(message = "recipientId can't be null/blank")
    private String recipientId;
    private String description;
    @NotNull(message = "amount can't be null/blank")
    @DecimalMin(value = "1.0", message = "amount must be greater than 0")
    private double amount;
    @NotNull(message = "paymentType can't be null/blank")
    private PaymentType paymentType;
}
