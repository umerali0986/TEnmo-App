package com.techelevator.dao;

import com.techelevator.tenmo.dao.JdbcTransferStatusDao;
import com.techelevator.tenmo.dao.JdbcUserDao;
import com.techelevator.tenmo.model.TransferStatus;
import com.techelevator.tenmo.model.User;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.junit.jupiter.api.Assertions.*;

public class JdbcTransferStatusDaoTest extends BaseDaoTests{

    private final static TransferStatus TRANSFER_STATUS_1 = new TransferStatus(1,"Pending");
    private final static TransferStatus TRANSFER_STATUS_2 = new TransferStatus(2,"Accepted");
    private final static TransferStatus TRANSFER_STATUS_3 = new TransferStatus(3,"Rejected");

    private JdbcTransferStatusDao sut;

    @Before
    public void setup() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        sut = new JdbcTransferStatusDao(jdbcTemplate);
    }


    @Test
    public void getTransferStatusById_return_correct_TransferStatus(){
        TransferStatus transferStatus = sut.getTransferStatusById(TRANSFER_STATUS_1.getTransferStatusId());

        Assert.assertNotNull("getTransferStatusById() returned null transfer status.",transferStatus);

        assertTransferStatusMatch(TRANSFER_STATUS_1,transferStatus);
    }

    @Test
    public void getTransferStatusById_given_invalid_transferStatusId_return_null(){

        int invalidTransferStatusId = 100;
        TransferStatus transferStatus = sut.getTransferStatusById(invalidTransferStatusId);

        Assert.assertNull("getTransferStatusById() returned not null transfer status.",transferStatus);

    }
    private void assertTransferStatusMatch(TransferStatus expected, TransferStatus actual){
        Assert.assertEquals("TransferStatus Id doesn't match",expected.getTransferStatusId(), actual.getTransferStatusId());
        Assert.assertEquals("TransferStatus description doesn't match",expected.getTransferStatusDesc(), actual.getTransferStatusDesc());
    }
}