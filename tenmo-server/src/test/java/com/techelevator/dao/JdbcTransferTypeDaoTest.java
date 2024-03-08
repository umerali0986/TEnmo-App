package com.techelevator.dao;

import com.techelevator.tenmo.dao.JdbcTransferStatusDao;
import com.techelevator.tenmo.dao.JdbcTransferTypeDao;
import com.techelevator.tenmo.model.TransferStatus;
import com.techelevator.tenmo.model.TransferType;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;

public class JdbcTransferTypeDaoTest extends BaseDaoTests {

    private final static TransferType TRANSFER_TYPE_1 = new TransferType(1,"Request");
    private final static TransferType TRANSFER_TYPE_2 = new TransferType(2,"Send");

    private JdbcTransferTypeDao sut;

    @Before
    public void setup() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        sut = new JdbcTransferTypeDao(jdbcTemplate);
    }

    @Test
    public void getTransferTypeById_returns_correct_transferType(){

        TransferType transferType = sut.getTransferTypeById(TRANSFER_TYPE_1.getTransferTypeId());

        Assert.assertNotNull("getTransferTypeById() returned null transferType", transferType);

        assertTransferTypeMatch(TRANSFER_TYPE_1,transferType);
    }

    @Test
    public void getTransferTypeById_given_invalid_transferTypeId_returns_null(){

        int invalidTransferTypeId = 100;

        TransferType transferType = sut.getTransferTypeById(invalidTransferTypeId);

        Assert.assertNull("getTransferTypeById() returned not null transferType", transferType);

    }

    private void assertTransferTypeMatch(TransferType expected, TransferType actual){
        Assert.assertEquals("TransferType Id doesn't match",expected.getTransferTypeId(), actual.getTransferTypeId());
        Assert.assertEquals("TransferType description doesn't match",expected.getTransferTypeDesc(), actual.getTransferTypeDesc());
    }

}
