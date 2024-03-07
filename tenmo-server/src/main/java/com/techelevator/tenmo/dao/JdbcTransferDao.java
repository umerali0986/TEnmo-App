package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.exception.DaoException;
import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Transfer;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class JdbcTransferDao implements TransferDao{

    private final JdbcTemplate jdbcTemplate;

    public JdbcTransferDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Transfer getTransferById(int id){
        Transfer transfer = null;
        String sql = "SELECT * FROM transfer WHERE transfer_id = ?;";

        try {
            SqlRowSet result = jdbcTemplate.queryForRowSet(sql, id);
            if(result.next()){
                transfer = mapRowToTransfer(result);
            }
        }catch (CannotGetJdbcConnectionException e){
            throw new DaoException("Unable to connect to server or database");
        }

        return transfer;
    }
    @Override
    public List<Transfer> getPendingTransferByAccountId(int accountId, int statusId) {

        List<Transfer> pendingTransfers = new ArrayList<>();
        String sql = "SELECT * FROM transfer WHERE (account_from = ? OR account_to = ?) AND transfer_status_id = ?;";

        try {
            SqlRowSet result = jdbcTemplate.queryForRowSet(sql, accountId, accountId, statusId);
            while (result.next()){
                pendingTransfers.add(mapRowToTransfer(result));
            }
        }catch (CannotGetJdbcConnectionException e){
            throw new DaoException("Unable to connect to server or database");
        }
        return pendingTransfers;

    }



    @Override
    public Transfer createReceipt(Transfer transfer) {
        Transfer newTransfer = null;


        String sql = "INSERT INTO transfer (transfer_type_id, transfer_status_id, account_from, account_to, amount)" +
                " VALUES (?,?,?,?,?) RETURNING transfer_id;";

        try{
            int newTransferId = jdbcTemplate.queryForObject(sql, int.class, transfer.getTransfer_type_id(),
                    transfer.getTransfer_status_id(), transfer.getAccount_from(), transfer.getAccount_to(), transfer.getAmount());
            newTransfer = getTransferById(newTransferId);
        }
        catch (CannotGetJdbcConnectionException e){
            throw new DaoException("Unable to connect to server or database");
        }
        catch (DataIntegrityViolationException e){
            throw new DaoException("Data integrity violation");
        }


        return newTransfer;
    }

    @Override
    public List<Transfer> getTransfersByAccountId(int accountId){
        List<Transfer> transfers = new ArrayList<>();
        String sql = "SELECT * FROM transfer WHERE account_from = ? OR account_to = ? ORDER BY transfer_id DESC;";

        try {
            SqlRowSet result = jdbcTemplate.queryForRowSet(sql, accountId, accountId);
            while (result.next()){
                transfers.add(mapRowToTransfer(result));
            }
        }catch (CannotGetJdbcConnectionException e){
            throw new DaoException("Unable to connect to server or database");
        }
        return transfers;
    }


    private Transfer mapRowToTransfer(SqlRowSet result) {
//        transfer_type_id, transfer_status_id, account_from, account_to, amount
        Transfer transfer = new Transfer();

        transfer.setTransfer_id(result.getInt("transfer_id"));
        transfer.setTransfer_type_id(result.getInt("transfer_type_id"));
        transfer.setTransfer_status_id(result.getInt("transfer_status_id"));
        transfer.setAccount_from(result.getInt("account_from"));
        transfer.setAccount_to(result.getInt("account_to"));
        transfer.setAmount(result.getBigDecimal("amount"));

        return transfer;
    }
}
