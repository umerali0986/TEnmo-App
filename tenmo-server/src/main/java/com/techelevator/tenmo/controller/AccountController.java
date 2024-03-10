package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.Dto.UpdateAccountDto;
import com.techelevator.tenmo.dao.JdbcAccountDao;
import com.techelevator.tenmo.dao.JdbcUserDao;
import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.User;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(path = "/balance", method = RequestMethod.GET)
    public BigDecimal getAccountBalance(Principal principal) {

        User user = jdbcUserDao.getUserByUsername(principal.getName());
        BigDecimal balance = jdbcAccountDao.getAccountBalance(user.getId());

        return balance;

    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(path = "/user/{id}/account", method = RequestMethod.GET)
    public Account getAccountByUserId(@PathVariable int id) {

        Account account = jdbcAccountDao.getAccountByUserId(id);
        return  account;

    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(path = "/account/updateBalance/transfer/{transferId}", method = RequestMethod.PUT)
    public void updateFunds(@RequestBody UpdateAccountDto updateAccountDto, @PathVariable int transferId) {

        jdbcAccountDao.updateFunds(updateAccountDto.getAmount(), updateAccountDto.getAccount(), updateAccountDto.isWithdraw(), transferId);

    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(path = "/balance/account/{id}", method = RequestMethod.GET)
    public BigDecimal getBalanceByAccountId(@PathVariable("id") int id){
        BigDecimal accountBalance = jdbcAccountDao.getBalanceByAccountId(id);
        return accountBalance;
    }

}
