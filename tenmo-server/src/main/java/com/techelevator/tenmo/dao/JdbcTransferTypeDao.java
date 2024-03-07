package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.exception.DaoException;
import com.techelevator.tenmo.model.TransferType;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

@Component
public class JdbcTransferTypeDao implements TransferTypeDao{

    private final JdbcTemplate jdbcTemplate;

    public JdbcTransferTypeDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    @Override
    public TransferType getTransferTypeById(int transferTypeId) {
        TransferType transferType = null;
        String sql = "SELECT * FROM transfer_type WHERE transfer_type_id = ?;";

        try {
            SqlRowSet result = jdbcTemplate.queryForRowSet(sql, transferTypeId);
            if(result.next()){
                transferType = mapRowToTransferType(result);
            }
        }catch (CannotGetJdbcConnectionException e){
            throw new DaoException("Unable to connect to server or database");
        }
        return transferType;
    }

    private TransferType mapRowToTransferType(SqlRowSet result) {

        TransferType transferType = new TransferType();
        transferType.setTransferTypeId(result.getInt("transfer_type_id"));
        transferType.setTransferTypeDesc(result.getString("transfer_type_desc"));

        return transferType;
    }
}
