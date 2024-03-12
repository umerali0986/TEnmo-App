package com.techelevator.dao;

import com.techelevator.tenmo.dao.JdbcAccountDao;
import com.techelevator.tenmo.dao.JdbcTransferDao;
import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;

import java.math.BigDecimal;
import java.util.List;

public class JdbcTransferDaoTests extends BaseDaoTests {
    protected static final Transfer TRANSFER_1 = new Transfer(3005, 2, 1, 2001,2002, new BigDecimal(1000.00));
    protected static final Transfer TRANSFER_2 = new Transfer(3002, 1, 2, 2002,2003, new BigDecimal(1000.00));
    private static final Transfer TRANSFER_3 = new Transfer(3003, 1, 3, 2003,2001, new BigDecimal(1000.00));
    protected static final Account ACCOUNT_1 = new Account(2001, 1001, new BigDecimal(1000.00));
    protected static final Account ACCOUNT_2 = new Account(2002, 1002, new BigDecimal(1000.00));
    protected static final Account ACCOUNT_3 = new Account(2003, 1003, new BigDecimal(1000.00));

    private JdbcTransferDao sut;

    @Before
    public void setup() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        sut = new JdbcTransferDao(jdbcTemplate, new JdbcAccountDao(jdbcTemplate));
    }


    @Test
    public void getTransferById_given_valid_id_should_return_transfer(){
        Transfer transfer = sut.getTransferById(TRANSFER_1.getTransfer_id());

        Assert.assertNotNull("getTransferById() returned null transfer", transfer);
        assertTransferMatch(TRANSFER_1, transfer);
    }

    @Test
    public void getPendingTransferByAccountId_should_return_correct_list_of_transfer(){
        List<Transfer> pendingTransfers = sut.getPendingTransferByAccountId(ACCOUNT_1.getAccount_id(), TRANSFER_1.getTransfer_status_id());

        Assert.assertEquals("Pending transfer list has incorrect number of transfers",1, pendingTransfers.size());
        assertTransferMatch(TRANSFER_1, pendingTransfers.get(0));
    }

//    @Test
//    public void createReceipt_should_return_new_transfer(){
//        Transfer testTransfer =  new Transfer(1, 2, 2002,2003, new BigDecimal(1000.00));
//        Transfer transferReceipt = sut.createReceipt(testTransfer);
//
//        testTransfer.setTransfer_id(transferReceipt.getTransfer_id());
//
//        Assert.assertNotNull("createReceipt() returned null", transferReceipt);
//        assertTransferMatch(testTransfer, transferReceipt);
//    }

    @Test
    public void getTransferByAccountId_should_return_correct_transfer(){
        List<Transfer> transfers = sut.getTransfersByAccountId(ACCOUNT_1.getAccount_id());

        Assert.assertEquals("getTransferByAccountId() did not return the correct amount", 1, transfers.size());
        assertTransferMatch(TRANSFER_1, transfers.get(0));
    }

    @Test
    public void updateTransactionStatus_should_update_successfully(){

        int numberOfRowsUpdate = sut.updateTransactionStatus(2, TRANSFER_1.getTransfer_id());

        Assert.assertEquals("Incorrect number of rows updated",1, numberOfRowsUpdate);
        Assert.assertEquals("Incorrect status update", 2,sut.getTransferById(TRANSFER_1.getTransfer_id()).getTransfer_status_id());
    }


    private void assertTransferMatch(Transfer expected, Transfer actual){
        Assert.assertEquals("Transfer Id doesn't match",expected.getTransfer_id(), actual.getTransfer_id());
        Assert.assertEquals("Transfer status id doesn't match", expected.getTransfer_type_id(), actual.getTransfer_type_id());
    }

}
