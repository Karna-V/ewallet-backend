package com.rampnow.ewallet.controllers.user;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.rampnow.ewallet.payloads.commons.DataResponse;
import com.rampnow.ewallet.payloads.commons.GetAllData;
import com.rampnow.ewallet.payloads.user.UserPayload;
import com.rampnow.ewallet.services.user.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping(value = "/user")
public class UserController {

    @Value("${baseUrl}")
    private String baseUrl;

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/create")
    public ResponseEntity<DataResponse> createUser(@Valid @RequestBody UserPayload userPayload) {
        return new ResponseEntity<>(
                DataResponse.builder().result(userService.createUser(userPayload)).build(),
                HttpStatus.CREATED);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<DataResponse> updateUser(@PathVariable String id,
            @Valid @RequestBody UserPayload userPayload) {
        return new ResponseEntity<>(
                DataResponse.builder().result(userService.updateUser(id, userPayload)).build(),
                HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<DataResponse> updateUser(@PathVariable String id) {
        return new ResponseEntity<>(
                DataResponse.builder().result(userService.deleteUser(id)).build(),
                HttpStatus.OK);
    }

    @GetMapping("/get/all/{skip}/{limit}")
    public ResponseEntity<DataResponse> getAllUsers(HttpServletRequest request, @PathVariable Integer skip,
            @PathVariable Integer limit, @RequestParam(required = false) String searchText) {
        GetAllData data = new GetAllData();
        data.setData(userService.getAllUsers(skip, limit, searchText));
        String urlPath = "/user/get/all/" + (skip + 1) + "/" + limit;
        data.setNextUrl(getNextUrl(request, urlPath));
        return new ResponseEntity<>(DataResponse.builder().result(data).build(), HttpStatus.OK);
    }

    public String getNextUrl(HttpServletRequest request, String urlPath) {
        StringBuilder sb = new StringBuilder(baseUrl);
        sb.append(urlPath);
        if (request.getQueryString() != null)
            sb.append("?").append(request.getQueryString());
        return sb.toString();
    }
}
