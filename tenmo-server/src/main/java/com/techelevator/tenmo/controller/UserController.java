package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.JdbcUserDao;
import com.techelevator.tenmo.model.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RestController
public class UserController {
    private final JdbcUserDao jdbcUserDao;

    public UserController(JdbcUserDao jdbcUserDao) {
        this.jdbcUserDao = jdbcUserDao;
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(path = "/users", method = RequestMethod.GET)
    public List<User> getOtherUsers(Principal principal){
        List<User> otherUsers = null;
        otherUsers = jdbcUserDao.getOtherUsers(principal.getName());
        return otherUsers;
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(path = "/user/{id}", method = RequestMethod.GET)
    public User getUserById(@PathVariable int id){
        User user = null;
        user = jdbcUserDao.getUserById(id);
        return user;
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(path = "/account/{id}/user", method = RequestMethod.GET)
    public User getUserByAccountId(@PathVariable("id") int accountId){
        return jdbcUserDao.getUserByAccountId(accountId);
    }

}
