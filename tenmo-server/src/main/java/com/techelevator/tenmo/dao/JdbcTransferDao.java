package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.exception.DaoException;
import com.techelevator.tenmo.model.Transfer;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
public class JdbcTransferDao implements TransferDao{

    private final JdbcTemplate jdbcTemplate;
    private JdbcAccountDao jdbcAccountDao;

    public JdbcTransferDao(JdbcTemplate jdbcTemplate, JdbcAccountDao jdbcAccountDao) {
        this.jdbcTemplate = jdbcTemplate;
        this.jdbcAccountDao = jdbcAccountDao;

    }
//    public  JdbcTransferDao(JdbcTemplate jdbcTemplate) {
//        this.jdbcTemplate = jdbcTemplate;
//    }


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
        String sql = "SELECT * FROM transfer WHERE (account_from = ? OR account_to = ?) AND transfer_status_id = ? ORDER BY transaction_date DESC;";

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
        BigDecimal senderCurrentBalance = jdbcAccountDao.getBalanceByAccountId(transfer.getAccount_from()).subtract(transfer.getAmount());
        BigDecimal receiverCurrentBalance = jdbcAccountDao.getBalanceByAccountId(transfer.getAccount_to()).add(transfer.getAmount());
// line 70 "current_account_to_balance, current_account_from_balance," line 71 "?,?,"
//TODO fix minor Balance issue
        String sql = "INSERT INTO transfer (transfer_type_id, transfer_status_id, account_from, account_to, amount, transaction_date)" +
                " VALUES (?,?,?,?,?, CURRENT_TIMESTAMP) RETURNING transfer_id;";
// line 80 receiverCurrentBalance, senderCurrentBalance
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
        String sql = "SELECT * FROM transfer WHERE (account_from = ? OR account_to = ?) AND transfer_status_id != 3 ORDER BY transaction_date DESC;";

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

    @Override
    public int updateTransactionStatus(int statusId, int transferId){
        int numberOfRowsUpdated = 0;

        String sql = "UPDATE transfer SET transfer_status_id = ? WHERE transfer_id = ?;";
        try{
            numberOfRowsUpdated = jdbcTemplate.update(sql, statusId, transferId);
        }
        catch (CannotGetJdbcConnectionException e){
            throw new DaoException("Unable to connect to server or database");
        }
        catch (DataIntegrityViolationException e){
            throw new DaoException("Data integrity violation");
        }
        return numberOfRowsUpdated;
    }

    @Override
    public int updateBalanceAtTimeOfTransaction(int transferId, boolean isWithdraw, BigDecimal newBalance) {

        int numberOfRowUpdated = 0;
        String sql;

        if (isWithdraw) {
            sql = "UPDATE transfer SET current_account_from_balance = ?, transaction_date = CURRENT_TIMESTAMP WHERE transfer_id = ?;";
            try {

                numberOfRowUpdated = jdbcTemplate.update(sql, newBalance, transferId);

            } catch (CannotGetJdbcConnectionException e) {

                throw new DaoException("Unable to connect to database or server.");

            } catch (DataIntegrityViolationException e) {

                throw new DaoException("Data Integrity Violation.");

            }

        }  else {
            sql = "UPDATE transfer SET current_account_to_balance = ?, transaction_date = CURRENT_TIMESTAMP WHERE transfer_id = ?;";
            try {

                numberOfRowUpdated = jdbcTemplate.update(sql, newBalance, transferId);

            } catch (CannotGetJdbcConnectionException e) {

                throw new DaoException("Unable to connect to database or server.");

            } catch (DataIntegrityViolationException e) {

                throw new DaoException("Data Integrity Violation.");

            }

        } return numberOfRowUpdated;

    }



    private Transfer mapRowToTransfer(SqlRowSet result) {
        Transfer transfer = new Transfer();

        transfer.setTransfer_id(result.getInt("transfer_id"));
        transfer.setTransfer_type_id(result.getInt("transfer_type_id"));
        transfer.setTransfer_status_id(result.getInt("transfer_status_id"));
        transfer.setAccount_from(result.getInt("account_from"));
        transfer.setAccount_to(result.getInt("account_to"));
        transfer.setAmount(result.getBigDecimal("amount"));
        transfer.setCurrentAccountFromBalance(result.getBigDecimal("current_account_from_balance"));
        transfer.setCurrentAccountToBalance(result.getBigDecimal("current_account_to_balance"));
        transfer.setTransactionDate(result.getTimestamp("transaction_date"));


        return transfer;
    }
}
