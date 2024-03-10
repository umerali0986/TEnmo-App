package com.techelevator.tenmo.services;

import com.techelevator.tenmo.Dto.UpdateAccountDto;
import com.techelevator.tenmo.model.Account;
import com.techelevator.util.BasicLogger;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;

public class AccountService {


    private String authToken;
    private final String API_BASE_URL = "http://localhost:8080";
    private RestTemplate restTemplate = new RestTemplate();

    public BigDecimal getBalance() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(authToken);
        HttpEntity<Void> entity =new HttpEntity<>(headers);
        BigDecimal balance = new BigDecimal(0);

        try {
            ResponseEntity<BigDecimal> response = restTemplate.exchange(API_BASE_URL + "/balance", HttpMethod.GET, entity, BigDecimal.class);
            balance = response.getBody();
        } catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }

        return balance;

    }

    public Account getAccountByUserId(int userId) {

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(authToken);
        HttpEntity<Void> entity =new HttpEntity<>(headers);
        Account account = null;

        try {

            ResponseEntity<Account> response = restTemplate.exchange(API_BASE_URL + "/user/" + userId + "/account", HttpMethod.GET, entity, Account.class);
            account = response.getBody();

        } catch (RestClientResponseException | ResourceAccessException e) {

            BasicLogger.log(e.getMessage());

        } return account;

    }

    public void updateAccountBalance(UpdateAccountDto updateAccountDto, int transferId){
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(authToken);
        HttpEntity<UpdateAccountDto> entity =new HttpEntity<>(updateAccountDto, headers);
        try{
            ResponseEntity<UpdateAccountDto> response = restTemplate.exchange(API_BASE_URL + "/account/updateBalance/transfer/" + transferId, HttpMethod.PUT, entity, UpdateAccountDto.class);

        } catch (RestClientResponseException | ResourceAccessException e) {

            BasicLogger.log(e.getMessage());

        }
    }

    public void setAuthToken(String token) {
        authToken = token;
    }

}
