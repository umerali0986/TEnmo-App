package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.JdbcTransferDao;
import com.techelevator.tenmo.model.Transfer;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

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
}
