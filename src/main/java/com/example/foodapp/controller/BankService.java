package com.example.foodapp.controller;

import com.example.foodapp.dto.Transaction;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name="fund-transfer-service")
public interface BankService {

    @PostMapping(path = "/bankcustomer/transfer")
    public String initiateTransfer(@RequestBody Transaction transaction);

}
