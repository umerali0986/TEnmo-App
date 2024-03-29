package com.techelevator.tenmo.model;

import java.math.BigDecimal;

public class Account {
    //properties
    int account_id;
    int userId;
    BigDecimal balance;

    //constructor
    public Account() {
    }

    public Account(int account_id, int userId, BigDecimal balance) {
        this.account_id = account_id;
        this.userId = userId;
        this.balance = balance;
    }

    //getters & setters
    public int getAccount_id() {
        return account_id;
    }

    public void setAccount_id(int account_id) {
        this.account_id = account_id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
}
