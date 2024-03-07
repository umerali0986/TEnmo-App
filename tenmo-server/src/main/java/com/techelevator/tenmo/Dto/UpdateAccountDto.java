package com.techelevator.tenmo.Dto;

import com.techelevator.tenmo.model.Account;

import java.math.BigDecimal;

public class UpdateAccountDto {
    //properties
    private Account account;
    private BigDecimal amount;
    private boolean withdraw;

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public boolean isWithdraw() {
        return withdraw;
    }

    public void setWithdaw(boolean withdraw) {
        this.withdraw = withdraw;
    }
}
