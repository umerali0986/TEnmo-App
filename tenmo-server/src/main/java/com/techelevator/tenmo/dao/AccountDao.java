package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;

import java.math.BigDecimal;

public interface AccountDao {
    /**
     * User should be able to get account balance.
     */

    BigDecimal getAccountBalance(int userId);

    Account getAccountByUserId(int id);

    int updateFunds(BigDecimal amount, Account account, boolean isWithdraw, int transferId);

    BigDecimal getBalanceByAccountId(int id);
}
