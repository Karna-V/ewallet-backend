package com.rampnow.ewallet.controllers.transaction;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.rampnow.ewallet.payloads.commons.DataResponse;
import com.rampnow.ewallet.payloads.commons.GetAllData;
import com.rampnow.ewallet.payloads.transaction.TransactionPayload;
import com.rampnow.ewallet.services.transaction.TransactionService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping(value = "/transaction")
public class TransactionController {

    @Value("${baseUrl}")
    private String baseUrl;

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping("/create")
    public ResponseEntity<DataResponse> createUser(@Valid @RequestBody TransactionPayload transactionPayload) {
        return new ResponseEntity<>(
                DataResponse.builder().result(transactionService.createTransaction(transactionPayload)).build(),
                HttpStatus.OK);
    }

    @GetMapping("/{userId}/get/all/{skip}/{limit}")
    public ResponseEntity<DataResponse> getAllTransactions(HttpServletRequest request, @PathVariable String userId,
            @PathVariable Integer skip, @PathVariable Integer limit, @RequestParam(required = false) String searchText,
            @RequestParam(required = false) Direction sort) {
        GetAllData data = new GetAllData();
        data.setData(transactionService.getAllTransactions(userId, skip, limit, searchText, sort));
        String urlPath = "/transaction/" + userId + "/get/all/" + (skip + 1) + "/" + limit;
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
