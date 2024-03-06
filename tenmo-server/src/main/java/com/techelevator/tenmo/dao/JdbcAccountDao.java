package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.exception.DaoException;
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

}
