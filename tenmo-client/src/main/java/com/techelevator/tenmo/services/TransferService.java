package com.techelevator.tenmo.services;

import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.util.BasicLogger;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.util.List;


public class TransferService {
    private String authToken;
    private final String API_BASE_URL = "http://localhost:8080";
    private RestTemplate restTemplate = new RestTemplate();


    public Transfer createReceipt(Transfer transfer){
        Transfer createdTransfer = null;

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(authToken);
        HttpEntity<Transfer> entity = new HttpEntity<>(transfer,headers);

        try{
            ResponseEntity<Transfer> response = restTemplate.exchange(API_BASE_URL + "/send", HttpMethod.POST, entity, Transfer.class);
            createdTransfer = response.getBody();
        }
        catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }

        return createdTransfer;
    }

    public Transfer[] viewTransferHistory(int accountId){
        Transfer[] transferHistory = null;
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(authToken);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        try{
            ResponseEntity<Transfer[]> response = restTemplate.exchange(API_BASE_URL + "/account/" + accountId + "/transfer/history", HttpMethod.GET, entity, Transfer[].class);
            transferHistory = response.getBody();
        }
        catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
        return transferHistory;
    }

    public void setAuthToken(String token) {
        authToken = token;
    }
}
