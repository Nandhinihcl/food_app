package com.example.foodapp.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
public class Transaction {
    private Long fromAccountNumber;
    private Long toAccountNumber;
    private Double amount;
    private String comments;


}
