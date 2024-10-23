package com.rampnow.ewallet.repositories.user;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import com.rampnow.ewallet.models.user.User;

@Repository
public interface UserRepo extends ReactiveMongoRepository<User, String> {

}
