package com.techelevator.dao;

import com.techelevator.tenmo.Dto.UpdateAccountDto;
import com.techelevator.tenmo.dao.JdbcAccountDao;
import com.techelevator.tenmo.dao.JdbcTransferStatusDao;
import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.User;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;

import java.math.BigDecimal;

public class JdbcAccountDaoTest extends BaseDaoTests {

    protected static final User USER_1 = new User(1001, "user4", "user1", "USER");
    protected static final User USER_2 = new User(1002, "user5", "user2", "USER");
    private static final User USER_3 = new User(1003, "user6", "user3", "USER");
    protected static final Account ACCOUNT_1 = new Account(2001, 1001, new BigDecimal(1000.00));
    protected static final Account ACCOUNT_2 = new Account(2002, 1002, new BigDecimal(1000.00));
    protected static final Account ACCOUNT_3 = new Account(2003, 1003, new BigDecimal(1000.00));

    private static final UpdateAccountDto updateAccountDto = new UpdateAccountDto(ACCOUNT_1, new BigDecimal(50), true);
    private JdbcAccountDao sut;

    @Before
    public void setup() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        sut = new JdbcAccountDao(jdbcTemplate);
    }
    @Test
    public void getAccountBalance_Should_Return_Correct_Balance() {
        BigDecimal balance = sut.getAccountBalance(USER_1.getId());

        Assert.assertNotNull("getAccountBalance() returned null balance.", balance);
        Assert.assertEquals("Get account balance return correct balance.",0, balance.compareTo(ACCOUNT_1.getBalance()));
    }

    @Test
    public void getAccountByUserId_Should_Return_Correct_Account() {
        Account account = sut.getAccountByUserId(USER_1.getId());

        Assert.assertNotNull("getAccountByUerId() returned null Account", account);
        assertAccountMatch(ACCOUNT_1, account);
    }

//    @Test
//    public void updateFunds_Should_Update_Account_Balance() {
//        int numberOfRowUpdated = sut.updateFunds(updateAccountDto.getAmount(),updateAccountDto.getAccount(), updateAccountDto.isWithdraw());
//
//        Assert.assertEquals(1, numberOfRowUpdated);
//    }

    @Test
    public void getBalanceByAccountId_given_valid_id_return_balance(){
        BigDecimal balance = sut.getBalanceByAccountId(ACCOUNT_1.getAccount_id());

        Assert.assertNotNull("getBalanceByAccountId() returned null balance", balance);

        Assert.assertEquals("Account balance doesn't match",0, ACCOUNT_1.getBalance().compareTo(balance));

    }

    private void assertAccountMatch(Account expected, Account actual){
        Assert.assertEquals("Account Id doesn't match",expected.getAccount_id(), actual.getAccount_id());
        Assert.assertEquals("Account balance doesn't match",0, expected.getBalance().compareTo(actual.getBalance()));
    }
}
