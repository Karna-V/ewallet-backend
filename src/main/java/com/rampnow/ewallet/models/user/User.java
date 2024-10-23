package com.rampnow.ewallet.models.user;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import com.rampnow.ewallet.payloads.user.UserPayload;
import com.rampnow.ewallet.utils.GenerateId;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "users")
public class User {
    @Transient
    private static final String shortCode = "USR";
    @Id
    private String id;
    private String name;
    private String username;
    private String emailId;
    private Phone phone;
    private Double walletBalance;
    private Date createdOn;
    private Date updatedOn;

    public User(UserPayload userPayload) {
        this.id = GenerateId.withPrefix(shortCode);
        this.name = userPayload.getName();
        this.username = userPayload.getUsername();
        this.emailId = userPayload.getEmailId();
        this.phone = userPayload.getPhone();
        this.walletBalance = 0.0;
        this.createdOn = new Date();
        this.updatedOn = new Date();
    }

    public void update(UserPayload userPayload) {
        this.name = userPayload.getName();
        this.username = userPayload.getUsername();
        this.emailId = userPayload.getEmailId();
        this.phone = userPayload.getPhone();
        this.updatedOn = new Date();
    }

    public void addWalletBalance(double amount) {
        this.walletBalance += amount;
    }

    public void subtractWalletBalance(double amount) {
        this.walletBalance -= amount;
    }
}
