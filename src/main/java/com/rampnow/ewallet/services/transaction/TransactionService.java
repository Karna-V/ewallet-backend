package com.rampnow.ewallet.services.transaction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.bson.Document;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import com.rampnow.ewallet.models.transaction.Transaction;
import com.rampnow.ewallet.models.user.User;
import com.rampnow.ewallet.payloads.transaction.TransactionPayload;
import com.rampnow.ewallet.repositories.transaction.TransactionRepo;
import com.rampnow.ewallet.repositories.user.UserRepo;

@Service
public class TransactionService {
    private final TransactionRepo transactionRepo;
    private final UserRepo userRepo;
    private final ReactiveMongoTemplate reactiveMongoTemplate;

    public TransactionService(TransactionRepo transactionRepo, UserRepo userRepo,
            ReactiveMongoTemplate reactiveMongoTemplate) {
        this.transactionRepo = transactionRepo;
        this.userRepo = userRepo;
        this.reactiveMongoTemplate = reactiveMongoTemplate;
    }

    public String createTransaction(TransactionPayload transactionPayload) {
        User sender = userRepo.findById(transactionPayload.getSenderId()).blockOptional()
                .orElseThrow(() -> new IllegalArgumentException("invalid senderId"));
        User recipient = userRepo.findById(transactionPayload.getRecipientId()).blockOptional()
                .orElseThrow(() -> new IllegalArgumentException("invalid recipientId"));
        if (sender.getWalletBalance() >= transactionPayload.getAmount()) {
            sender.subtractWalletBalance(transactionPayload.getAmount());
            recipient.addWalletBalance(transactionPayload.getAmount());
        } else {
            throw new IllegalArgumentException("you don't have enough balance to make transaction");
        }
        userRepo.saveAll(Arrays.asList(sender, recipient)).subscribe();
        Transaction transaction = new Transaction(transactionPayload);
        transactionRepo.save(transaction).subscribe();
        return transaction.getId();
    }

    public List<Document> getAllTransactions(String userId, int skip, int limit, String searchText,
            Direction sortDirection) {
        List<AggregationOperation> operations = new ArrayList<>();
        operations.add(Aggregation.match(Criteria.where("senderId").is(userId)));
        operations.add(Aggregation.lookup("users", "recipientId", "_id", "recipientData"));
        operations.add(Aggregation.unwind("recipientData", true));
        if (searchText != null && !searchText.isEmpty()) {
            Criteria searchCriteria = new Criteria().orOperator(
                    Criteria.where("recipientData.name").regex(searchText, "i"),
                    Criteria.where("recipientData.username").regex(searchText, "i"),
                    Criteria.where("recipientData.emailId").regex(searchText, "i"),
                    Criteria.where("recipientData.phone.number").regex(searchText, "i"));
            operations.add(Aggregation.match(searchCriteria));
        }
        if (sortDirection != null) {
            operations.add(Aggregation.sort(sortDirection, "transactionDate"));
        } else
            operations.add(Aggregation.sort(Direction.DESC, "transactionDate"));
        operations.add(Aggregation.skip(skip));
        operations.add(Aggregation.limit(limit));
        operations.add(
                Aggregation
                        .project("senderId", "recipientId", "transactionDate", "amount", "paymentType", "status")
                        .and("_id").as("id")
                        .and("recipientData.name").as("recipientData.name"));
        Aggregation aggregation = Aggregation.newAggregation(operations);
        return reactiveMongoTemplate.aggregate(aggregation, "transactions", Document.class).collectList()
                .blockOptional().orElse(Collections.emptyList());
    }
}
