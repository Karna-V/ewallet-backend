package com.rampnow.ewallet.services.user;

import java.util.*;

import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.rampnow.ewallet.models.user.User;
import com.rampnow.ewallet.payloads.user.UserPayload;
import com.rampnow.ewallet.repositories.user.UserRepo;

@Service
public class UserService {

    private final UserRepo userRepo;
    private final ReactiveMongoTemplate reactiveMongoTemplate;

    public UserService(UserRepo userRepo, ReactiveMongoTemplate reactiveMongoTemplate) {
        this.userRepo = userRepo;
        this.reactiveMongoTemplate = reactiveMongoTemplate;
    }

    public String createUser(UserPayload userPayload) {
        User user = new User(userPayload);
        userRepo.save(user).subscribe();
        return user.getId();
    }

    public String updateUser(String id, UserPayload userPayload) {
        User user = userRepo.findById(id).blockOptional()
                .orElseThrow(() -> new IllegalArgumentException("user not found"));
        user.update(userPayload);
        return user.getId();
    }

    public String deleteUser(String id) {
        userRepo.deleteById(id).subscribe();
        return "User deleted successfully";
    }

    public List<User> getAllUsers(int skip, int limit, String searchText) {
        Query query = new Query();
        query.skip(skip).limit(limit);
        if (searchText != null && !searchText.isEmpty()) {
            Criteria criteria = new Criteria().orOperator(Criteria.where("name").regex(searchText, "i"),
                    Criteria.where("username").regex(searchText, "i"), Criteria.where("emailId").regex(searchText, "i"),
                    Criteria.where("phone.number").regex(searchText, "i"));
            query.addCriteria(criteria);
        }
        return reactiveMongoTemplate.find(query, User.class).collectList().blockOptional()
                .orElse(Collections.emptyList());
    }
}
