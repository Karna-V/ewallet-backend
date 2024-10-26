package com.rampnow.ewallet.services.user;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.rampnow.ewallet.models.user.User;
import com.rampnow.ewallet.payloads.user.LoginReq;
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

    public User loginUser(LoginReq loginReq) {
        User user = userRepo.findByUsernameAndPassword(loginReq.getUsername(), loginReq.getPassword()).blockOptional()
                .orElseThrow(() -> new IllegalArgumentException("invalid username/password"));
        return user.loginRes();
    }

    public User createUser(UserPayload userPayload) {
        if (checkIfPresent("username", userPayload.getUsername()))
            throw new IllegalArgumentException("username already exists");
        if (checkIfPresent("emailId", userPayload.getEmailId()))
            throw new IllegalArgumentException("emailId already exists");
        if (checkIfPresent("phone.number", userPayload.getPhone().getNumber()))
            throw new IllegalArgumentException("phone number already exists");

        User user = new User(userPayload);
        userRepo.save(user).subscribe();
        return user.loginRes();
    }

    public boolean checkIfPresent(String key, Object value) {
        return reactiveMongoTemplate.exists(new Query().addCriteria(Criteria.where(key).is(value)), User.class)
                .blockOptional().orElse(false);
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

    public User getById(String id) {
        User user = userRepo.findById(id).blockOptional()
                .orElseThrow(() -> new IllegalArgumentException("user not found"));
        return user.loginRes();
    }

    public List<User> getAllUsers(int skip, int limit, String currentUser, String searchText) {
        Query query = new Query().addCriteria(Criteria.where("id").ne(currentUser));
        query.skip(skip).limit(limit);
        if (searchText != null && !searchText.isEmpty()) {
            Criteria criteria = new Criteria().orOperator(Criteria.where("name").regex(searchText, "i"),
                    Criteria.where("username").regex(searchText, "i"),
                    Criteria.where("emailId").regex(searchText, "i"),
                    Criteria.where("phone.number").regex(searchText, "i"));
            query.addCriteria(criteria);
        }
        List<User> data = reactiveMongoTemplate.find(query, User.class).collectList().blockOptional()
                .orElse(Collections.emptyList());
        data = data.stream().map(User::minimalRes).collect(Collectors.toList());
        return data;
    }
}
