package com.rampnow.ewallet.services.transaction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
        Transaction transaction = new Transaction(transactionPayload);
        transactionRepo.save(transaction).subscribe();
        return transaction.getId();
    }

    public List<Object> getAllTransactions(String userId, int skip, int limit, String searchText,
            Direction sortDirection) {
        List<AggregationOperation> operations = new ArrayList<>();
        operations.add(Aggregation.match(Criteria.where("senderId").is(userId)));
        operations.add(Aggregation.lookup("users", "recipientId", "id", "recipientData"));
        if (searchText != null && !searchText.isEmpty()) {
            Criteria searchCriteria = new Criteria().orOperator(Criteria.where("name").regex(searchText, "i"),
                    Criteria.where("username").regex(searchText, "i"), Criteria.where("emailId").regex(searchText, "i"),
                    Criteria.where("phone.number").regex(searchText, "i"));
            operations.add(Aggregation.match(searchCriteria));
        }
        if (sortDirection != null) {
            operations.add(Aggregation.sort(sortDirection, "transactionDate"));
        } else
            operations.add(Aggregation.sort(Direction.DESC, "transactionDate"));
        operations.add(Aggregation.skip(skip));
        operations.add(Aggregation.limit(limit));
        Aggregation aggregation = Aggregation.newAggregation(operations);
        return reactiveMongoTemplate.aggregate(aggregation, Transaction.class, Object.class).collectList()
                .blockOptional().orElse(Collections.emptyList());
    }
}
