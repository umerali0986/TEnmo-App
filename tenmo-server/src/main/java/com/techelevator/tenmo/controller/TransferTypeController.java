package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.JdbcTransferDao;
import com.techelevator.tenmo.dao.JdbcTransferTypeDao;
import com.techelevator.tenmo.model.TransferType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TransferTypeController {

    private final JdbcTransferTypeDao jdbcTransferTypeDao;

    public TransferTypeController(JdbcTransferTypeDao jdbcTransferTypeDao) {
        this.jdbcTransferTypeDao = jdbcTransferTypeDao;
    }

//    @PreAuthorize("isAuthenticated()")
    @RequestMapping(path = "transfer/type/{id}", method = RequestMethod.GET)
    public TransferType getTransferTypeById(@PathVariable("id") int transferTypeId){
        return jdbcTransferTypeDao.getTransferTypeById(transferTypeId);
    }
}
