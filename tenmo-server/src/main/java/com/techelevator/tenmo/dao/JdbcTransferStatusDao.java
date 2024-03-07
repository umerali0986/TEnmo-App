package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.exception.DaoException;
import com.techelevator.tenmo.model.TransferStatus;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

@Component
public class JdbcTransferStatusDao implements TransferStatusDao{

    private final JdbcTemplate jdbcTemplate;

    public JdbcTransferStatusDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public TransferStatus getTransferStatusById(int transferStatusId) {

        TransferStatus transferStatus = null;

        String sql = "SELECT * FROM transfer_status WHERE transfer_status_id = ?;";

        try {
            SqlRowSet result = jdbcTemplate.queryForRowSet(sql, transferStatusId);
            if(result.next()){
                transferStatus = mapRowToTransferStatus(result);
            }
        }catch (CannotGetJdbcConnectionException e){
            throw new DaoException("Unable to connect to server or database");
        }

        return transferStatus;
    }

    private TransferStatus mapRowToTransferStatus(SqlRowSet result) {
        TransferStatus transferStatus = new TransferStatus();

        transferStatus.setTransferStatusId(result.getInt("transfer_status_id"));
        transferStatus.setTransferStatusDesc(result.getString("transfer_status_desc"));

        return transferStatus;
    }
}
