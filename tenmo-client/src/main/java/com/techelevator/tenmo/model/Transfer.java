package com.techelevator.tenmo.model;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class Transfer {

    private int transfer_type_id;
    private int transfer_status_id;
    private int account_from;
    private int account_to;
    private BigDecimal amount;
    private BigDecimal currentAccountToBalance;
    private BigDecimal currentAccountFromBalance;
    private Timestamp transactionDate;

    public BigDecimal getCurrentAccountToBalance() {
        return currentAccountToBalance;
    }

    public void setCurrentAccountToBalance(BigDecimal currentAccountToBalance) {
        this.currentAccountToBalance = currentAccountToBalance;
    }

    public BigDecimal getCurrentAccountFromBalance() {
        return currentAccountFromBalance;
    }

    public void setCurrentAccountFromBalance(BigDecimal currentAccountFromBalance) {
        this.currentAccountFromBalance = currentAccountFromBalance;
    }

    public Timestamp getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(Timestamp transactionDate) {
        this.transactionDate = transactionDate;
    }

    private int transfer_id;


    public int getTransfer_type_id() {
        return transfer_type_id;
    }

    public void setTransfer_type_id(int transfer_type_id) {
        this.transfer_type_id = transfer_type_id;
    }

    public int getTransfer_status_id() {
        return transfer_status_id;
    }

    public void setTransfer_status_id(int transfer_status_id) {
        this.transfer_status_id = transfer_status_id;
    }

    public int getAccount_from() {
        return account_from;
    }

    public void setAccount_from(int account_from) {
        this.account_from = account_from;
    }

    public int getAccount_to() {
        return account_to;
    }

    public void setAccount_to(int account_to) {
        this.account_to = account_to;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public int getTransfer_id() {
        return transfer_id;
    }

    @Override
    public String toString(){
        return "Sender = " + getAccount_from() + ", Receiver = " + getAccount_to();
    }
}
