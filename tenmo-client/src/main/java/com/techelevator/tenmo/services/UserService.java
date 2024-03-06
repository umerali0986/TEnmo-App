package com.techelevator.tenmo.services;

import com.techelevator.tenmo.model.User;
import com.techelevator.util.BasicLogger;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.List;

public class UserService {

    private String authToken;
    private final String API_BASE_URL = "http://localhost:8080";
    private RestTemplate restTemplate = new RestTemplate();

    public User[] getOtherUsers(){
        User[] users = null;
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(authToken);
        HttpEntity<Void> entity =new HttpEntity<>(headers);

        try {
            ResponseEntity<User[]> response = restTemplate.exchange(API_BASE_URL + "/users", HttpMethod.GET, entity, User[].class);
            users = response.getBody();
        } catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
        return users;
    }

    public void setAuthToken(String token) {
        authToken = token;
    }
}
