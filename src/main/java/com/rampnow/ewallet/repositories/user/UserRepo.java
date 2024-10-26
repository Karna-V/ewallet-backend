package com.rampnow.ewallet.repositories.user;

import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import com.rampnow.ewallet.models.user.User;

import reactor.core.publisher.Mono;

@Repository
public interface UserRepo extends ReactiveMongoRepository<User, String> {

    @Query(value = "{username: ?0 , password: ?1}")
    Mono<User> findByUsernameAndPassword(String username, String password);

}
