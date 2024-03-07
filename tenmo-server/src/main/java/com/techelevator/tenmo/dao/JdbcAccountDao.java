package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.exception.DaoException;
import com.techelevator.tenmo.model.Account;
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

    public void updateFunds(BigDecimal amount, Account account, boolean isWithdraw) {

        String sql = "UPDATE account SET balance = ? WHERE account_id = ?;";
        BigDecimal newBalance;

        if (isWithdraw) {

            newBalance = account.getBalance().subtract(amount);

        } else {

            newBalance = account.getBalance().add(amount);

        } try {

            jdbcTemplate.update(sql, newBalance, account.getAccount_id());

        } catch (CannotGetJdbcConnectionException e) {

            throw new DaoException("Unable to connect to database or server.");

        } catch (DataIntegrityViolationException e) {

            throw new DaoException("Data Integrity Violation.");

        }

    }
    public Account mapRowToAccount(SqlRowSet result) {
        Account account = new Account();
        account.setAccount_id(result.getInt("account_id"));
        account.setBalance(result.getBigDecimal("balance"));
        account.setUserId(result.getInt("user_id"));
        return account;
    }

}
