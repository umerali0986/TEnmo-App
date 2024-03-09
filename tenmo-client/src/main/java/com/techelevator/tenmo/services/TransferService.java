package com.techelevator.tenmo.services;

import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.TransferStatus;
import com.techelevator.tenmo.model.TransferType;
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


    public Transfer getTransferById(int transferId){
        Transfer transfer = null;
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(authToken);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        try{
            ResponseEntity<Transfer> response = restTemplate.exchange(API_BASE_URL + "/transfer/" + transferId, HttpMethod.GET, entity, Transfer.class);
            transfer = response.getBody();
        }
        catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }

        return transfer;
    }

    public TransferType getTransferTypeById(int transferTypeId){
        TransferType transferType = null;
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(authToken);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        try{
            ResponseEntity<TransferType> response = restTemplate.exchange(API_BASE_URL + "/transfer/type/" + transferTypeId, HttpMethod.GET, entity, TransferType.class);
            transferType = response.getBody();
        }
        catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }

        return transferType;
    }

    public TransferStatus getTransferStatusById(int transferStatusId){


        TransferStatus transferStatus = null;
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(authToken);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        try{
            ResponseEntity<TransferStatus> response = restTemplate.exchange(API_BASE_URL + "/transfer/status/" + transferStatusId, HttpMethod.GET, entity, TransferStatus.class);
            transferStatus = response.getBody();
        }
        catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }

        return transferStatus;
    }

    public Transfer[] getPendingTransfersByAccountId(int accountId, int statusId) {

        Transfer[] pendingTransfers = null;
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(authToken);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        try{
            ResponseEntity<Transfer[]> response = restTemplate.exchange(API_BASE_URL + "/transfer/" + accountId + "/" + statusId, HttpMethod.GET, entity, Transfer[].class);
            pendingTransfers = response.getBody();
        }
        catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }

        return pendingTransfers;
    }

    public void updateTransactionStatus(int statusId, int transferId){
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(authToken);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        try{
            restTemplate.exchange(API_BASE_URL + "/transfer/" + transferId + "/status/update/" + statusId, HttpMethod.PUT, entity, Void.class);
        }
        catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
    }

    public void setAuthToken(String token) {
        authToken = token;
    }
}
