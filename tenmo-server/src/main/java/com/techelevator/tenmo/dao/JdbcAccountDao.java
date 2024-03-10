package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.exception.DaoException;
import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Transfer;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class JdbcAccountDao implements AccountDao{
    //properties
    private final JdbcTemplate jdbcTemplate;

    //constructors

    public  JdbcAccountDao (JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public BigDecimal getAccountBalance(int userId) {

        BigDecimal balance = null;
        String sql = "SELECT balance FROM account WHERE user_id = ?;";

        try {

            SqlRowSet results = jdbcTemplate.queryForRowSet(sql, userId);

            if (results.next()) {

                balance = new BigDecimal(results.getString("balance"));

            }

        } catch (CannotGetJdbcConnectionException e) {

            throw new DaoException("Unable to connect to database or server.");

        }

        return balance;

    }
    @Override
    public Account getAccountByUserId(int id) {

        Account account = null;
        String sql = "SELECT * FROM account WHERE user_id =?;";

        try {

            SqlRowSet results = jdbcTemplate.queryForRowSet(sql, id);

            if (results.next()) {

                account = mapRowToAccount(results);

            }

        } catch (CannotGetJdbcConnectionException e) {

            throw new DaoException("Unable to connect to database or server.");

        }

        return account;
    }

    @Override
    public int updateFunds(BigDecimal amount, Account account, boolean isWithdraw, int transferId) {
        JdbcAccountDao test = null;
        JdbcTransferDao jdbcTransferDao = new JdbcTransferDao(jdbcTemplate, test);
        int numberOfRowUpdated = 0;
        String sql = "UPDATE account SET balance = ? WHERE account_id = ?;";
        BigDecimal newBalance = new BigDecimal(0);


        if (isWithdraw) {

            newBalance = account.getBalance().subtract(amount);
            jdbcTransferDao.updateBalanceAtTimeOfTransaction(transferId, isWithdraw, newBalance);

//todo working here
        } else  {

            newBalance = account.getBalance().add(amount);
            jdbcTransferDao.updateBalanceAtTimeOfTransaction(transferId, isWithdraw, newBalance);

        }
        try {
           numberOfRowUpdated = jdbcTemplate.update(sql, newBalance, account.getAccount_id());

            System.out.println(numberOfRowUpdated);
        } catch (CannotGetJdbcConnectionException e) {

            throw new DaoException("Unable to connect to database or server.");

        } catch (DataIntegrityViolationException e) {

            throw new DaoException("Data Integrity Violation.");

        }

//        if (isWithdraw) {
//            jdbcTransferDao.updateBalanceAtTimeOfTransaction(transfer, isWithdraw, newBalance);
//        } else {
//            jdbcTransferDao.updateBalanceAtTimeOfTransaction(transfer, isWithdraw, newBalance);
//        }

        return numberOfRowUpdated;

    }

    @Override
    public BigDecimal getBalanceByAccountId(int id){
        BigDecimal balance = new BigDecimal(0);
        String sql = "SELECT balance FROM account WHERE account_id = ?;";

        try {

            SqlRowSet results = jdbcTemplate.queryForRowSet(sql, id);

            if (results.next()) {

                balance = new BigDecimal(results.getString("balance"));

            }


        } catch (CannotGetJdbcConnectionException e) {

            throw new DaoException("Unable to connect to database or server.");

        }
        return balance;
    }

    public Account mapRowToAccount(SqlRowSet result) {
        Account account = new Account();
        account.setAccount_id(result.getInt("account_id"));
        account.setBalance(result.getBigDecimal("balance"));
        account.setUserId(result.getInt("user_id"));
        return account;
    }

}
