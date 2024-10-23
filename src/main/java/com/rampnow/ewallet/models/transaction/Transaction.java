package com.rampnow.ewallet.models.transaction;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import com.rampnow.ewallet.payloads.transaction.TransactionPayload;
import com.rampnow.ewallet.utils.GenerateId;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "transactions")
public class Transaction {
    @Transient
    private static final String shortCode = "TRN";
    @Id
    private String id;
    private String senderId;
    private String recipientId;
    private String description;
    private Double amount;
    private PaymentType paymentType;
    private TransactionStatus status;
    private Date transactionDate;

    public Transaction(TransactionPayload transactionPayload) {
        this.id = GenerateId.withPrefix(shortCode);
        this.senderId = transactionPayload.getSenderId();
        this.recipientId = transactionPayload.getRecipientId();
        this.description = transactionPayload.getDescription();
        this.amount = transactionPayload.getAmount();
        this.paymentType = transactionPayload.getPaymentType();
        this.status = TransactionStatus.completed;
        this.transactionDate = new Date();
    }
}
