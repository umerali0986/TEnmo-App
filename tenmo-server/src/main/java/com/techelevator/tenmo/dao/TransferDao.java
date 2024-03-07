package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Transfer;

import java.util.List;

public interface TransferDao {

    Transfer createReceipt(Transfer transfer);

    Transfer getTransferById(int id);
    List<Transfer> getTransfersByAccountId(int accountId);
}
