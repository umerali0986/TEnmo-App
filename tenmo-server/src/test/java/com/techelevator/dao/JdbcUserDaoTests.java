package com.techelevator.dao;

import com.techelevator.tenmo.dao.JdbcUserDao;
import com.techelevator.tenmo.exception.DaoException;
import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.RegisterUserDto;
import com.techelevator.tenmo.model.User;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.math.BigDecimal;
import java.util.List;

public class JdbcUserDaoTests extends BaseDaoTests {
    protected static final User USER_1 = new User(1001, "user4", "user1", "USER");
    protected static final User USER_2 = new User(1002, "user5", "user2", "USER");
    private static final User USER_3 = new User(1003, "user6", "user3", "USER");

    private static final Account ACCOUNT_1 = new Account(2001,1001,new BigDecimal(1000));

    private JdbcUserDao sut;

    @Before
    public void setup() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        sut = new JdbcUserDao(jdbcTemplate);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getUserByUsername_given_null_throws_exception() {
        sut.getUserByUsername(null);
    }

    @Test
    public void getUserByUsername_given_invalid_username_returns_null() {
        Assert.assertNull(sut.getUserByUsername("invalid"));
    }

    @Test
    public void getUserByUsername_given_valid_user_returns_user() {
        User actualUser = sut.getUserByUsername(USER_1.getUsername());

        Assert.assertEquals(USER_1, actualUser);
    }

    @Test
    public void getUserById_given_invalid_user_id_returns_null() {
        User actualUser = sut.getUserById(-1);

        Assert.assertNull(actualUser);
    }

    @Test
    public void getUserById_given_valid_user_id_returns_user() {
        User actualUser = sut.getUserById(USER_1.getId());

        Assert.assertEquals(USER_1, actualUser);
    }

    @Test
    public void getUsers_returns_all_users() {
        List<User> users = sut.getUsers();

        Assert.assertNotNull(users);
        Assert.assertEquals(3, users.size());
        Assert.assertEquals(USER_1, users.get(0));
        Assert.assertEquals(USER_2, users.get(1));
        Assert.assertEquals(USER_3, users.get(2));
    }

    @Test(expected = DaoException.class)
    public void createUser_with_null_username() {
        RegisterUserDto registerUserDto = new RegisterUserDto();
        registerUserDto.setUsername(null);
        registerUserDto.setPassword(USER_1.getPassword());
        sut.createUser(registerUserDto);
    }

    @Test(expected = DaoException.class)
    public void createUser_with_existing_username() {
        RegisterUserDto registerUserDto = new RegisterUserDto();
        registerUserDto.setUsername(USER_1.getUsername());
        registerUserDto.setPassword(USER_3.getPassword());
        sut.createUser(registerUserDto);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createUser_with_null_password() {
        RegisterUserDto registerUserDto = new RegisterUserDto();
        registerUserDto.setUsername(USER_3.getUsername());
        registerUserDto.setPassword(null);
        sut.createUser(registerUserDto);
    }



//    @Test
//    public void createUser_creates_a_user() {
//        RegisterUserDto user = new RegisterUserDto();
//        user.setUsername("new");
//        user.setPassword("USER");
//
//        User createdUser = sut.createUser(user);
//
//        Assert.assertNotNull(createdUser);
//
//        User retrievedUser = sut.getUserByUsername(createdUser.getUsername());
//        Assert.assertEquals(retrievedUser, createdUser);
//    }

    @Test
    public void getOtherUsers_return_other_users(){
        List<User> otherUsers = sut.getOtherUsers(USER_1.getUsername());

        Assert.assertEquals("getOtherUsers() return incorrect number of users",2,otherUsers.size());

        assertUserMatch(USER_2, otherUsers.get(0));
        assertUserMatch(USER_3, otherUsers.get(1));
    }

    @Test
    public void getOtherUsers_given_invalid_username_return_allUsers(){

        String invalidUsername = "QJFHDKJASHUISA897";

        List<User> otherUsers = sut.getOtherUsers(invalidUsername);

        Assert.assertEquals("getOtherUsers() return incorrect number of users",3,otherUsers.size());
    }


    @Test
    public void getUserByAccountId_return_correct_user_by_accountId(){
        User user = sut.getUserByAccountId(ACCOUNT_1.getAccount_id());

        Assert.assertNotNull("getUserByAccountId() returned null user",user);

        assertUserMatch(USER_1,user);
    }

    @Test
    public void getUserByAccountId_given_invalid_accountId_return_null(){

        int invalidAccountId = 100;

        User user = sut.getUserByAccountId(invalidAccountId);

        Assert.assertNull("getUserByAccountId() returned not null user",user);

    }

    private void assertUserMatch(User expected, User actual){
        Assert.assertEquals("User Id doesn't match",expected.getId(), actual.getId());
        Assert.assertEquals("User name doesn't match",expected.getUsername(), actual.getUsername());
    }
}
