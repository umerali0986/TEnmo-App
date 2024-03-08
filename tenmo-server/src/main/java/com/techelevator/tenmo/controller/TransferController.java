package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.JdbcTransferDao;
import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Transfer;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
public class TransferController {

    private final JdbcTransferDao jdbcTransferDao;

    public TransferController(JdbcTransferDao jdbcTransferDao) {
        this.jdbcTransferDao = jdbcTransferDao;
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(path = "/send", method = RequestMethod.POST)
    public Transfer createReceipt(@RequestBody Transfer transfer){
        return jdbcTransferDao.createReceipt(transfer);
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(path = "/account/{id}/transfer/history", method = RequestMethod.GET)
    public List<Transfer> getTransfersByAccountId(@PathVariable("id") int accountId){
        return jdbcTransferDao.getTransfersByAccountId(accountId);
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(path = "/transfer/{id}", method = RequestMethod.GET)
    public Transfer getTransferById(@PathVariable("id") int transferId){
        return jdbcTransferDao.getTransferById(transferId);
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(path = "/transfer/{accountId}/{statusId}", method = RequestMethod.GET)
    List<Transfer> getPendingTransferByAccountId(@PathVariable("accountId") int accountId,@PathVariable("statusId") int statusId) {
        return jdbcTransferDao.getPendingTransferByAccountId(accountId, statusId);
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(path = "/transfer/{transferId}/status/update/{transferStatusId}", method = RequestMethod.PUT)
    public void updateTransactionStatus(@PathVariable("transferStatusId") int transferStatusId, @PathVariable("transferId") int transferId){
        jdbcTransferDao.updateTransactionStatus(transferStatusId, transferId);
    }
}
