package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Transfer;

import java.math.BigDecimal;
import java.util.List;

public interface TransferDao {

    Transfer createReceipt(Transfer transfer);

    Transfer getTransferById(int transferId);
    List<Transfer> getTransfersByAccountId(int accountId);
    List<Transfer> getPendingTransferByAccountId(int accountId, int statusId);
    int updateTransactionStatus(int statusId, int transferId);

    int updateBalanceAtTimeOfTransaction(int transferId, boolean isWithdraw, BigDecimal newBalance);
}
