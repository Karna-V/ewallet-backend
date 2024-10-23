package com.rampnow.ewallet.repositories.transaction;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import com.rampnow.ewallet.models.transaction.Transaction;

@Repository
public interface TransactionRepo extends ReactiveMongoRepository<Transaction, String> {

}
