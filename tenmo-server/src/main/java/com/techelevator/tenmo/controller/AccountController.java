package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.JdbcAccountDao;
import com.techelevator.tenmo.dao.JdbcUserDao;
import com.techelevator.tenmo.model.User;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.security.Principal;

@RestController
public class AccountController {

    private final JdbcAccountDao jdbcAccountDao;
    private final JdbcUserDao jdbcUserDao;

    public AccountController(JdbcAccountDao jdbcAccountDao, JdbcUserDao jdbcUserDao) {
        this.jdbcAccountDao = jdbcAccountDao;
        this.jdbcUserDao = jdbcUserDao;
    }

    //TODO finish this
    @PreAuthorize("isAuthenticated()")
    @RequestMapping(path = "/balance", method = RequestMethod.GET)
    public BigDecimal getAccountBalance(Principal principal) {

        User user = jdbcUserDao.getUserByUsername(principal.getName());
        BigDecimal balance = jdbcAccountDao.getAccountBalance(user.getId());

        return balance;

    }

}
