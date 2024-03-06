package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transfer;

public interface TransferDao {

    Transfer createReceipt(Transfer transfer);

    Transfer getTransferById(int id);
}
