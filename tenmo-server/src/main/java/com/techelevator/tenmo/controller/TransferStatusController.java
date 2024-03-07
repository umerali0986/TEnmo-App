package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.JdbcTransferStatusDao;
import com.techelevator.tenmo.model.TransferStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TransferStatusController {

    private final JdbcTransferStatusDao jdbcTransferStatusDao;

    public TransferStatusController(JdbcTransferStatusDao jdbcTransferStatusDao) {
        this.jdbcTransferStatusDao = jdbcTransferStatusDao;
    }

    @RequestMapping(path = "/transfer/status/{id}", method = RequestMethod.GET)
    public TransferStatus getTransferStatusById(@PathVariable("id") int transferStatusId){
        return jdbcTransferStatusDao.getTransferStatusById(transferStatusId);
    }
}
