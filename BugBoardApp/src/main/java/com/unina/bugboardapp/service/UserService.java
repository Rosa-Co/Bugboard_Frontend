package com.unina.bugboardapp.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.unina.bugboardapp.exception.UserException;
import com.unina.bugboardapp.model.User;
import com.unina.bugboardapp.dto.UserCreateRequest;

import java.io.IOException;

public class UserService {

    private final ApiClient apiClient;
    private final ObjectMapper mapper;

    public UserService() {
        this.apiClient = ApiClient.getInstance();
        this.mapper = new ObjectMapper();
        this.mapper.registerModule(new JavaTimeModule());
        this.mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    public User createUser(User newUser) throws UserException {
        try {
            UserCreateRequest request = new UserCreateRequest(newUser.getUsername(), newUser.getPassword(),
                    newUser.getType());
            String requestBody = mapper.writeValueAsString(request);
            String responseBody = apiClient.post("/users", requestBody);

            if (responseBody != null && !responseBody.isEmpty()) {
                return mapper.readValue(responseBody, User.class);
            }
            throw new UserException("User creation failed: Empty response from server");
        }catch (IOException | InterruptedException e){
            Thread.currentThread().interrupt();
            throw new UserException("Communication error during user creation.", e);
        }catch(Exception e){
            throw new UserException("User creation failed: unpredicted error.", e);
        }
    }

    public boolean existsUser(String email) throws IOException, InterruptedException {
        String responseBody = apiClient.get("/users/email/" + email);
        return (responseBody != null && !responseBody.isEmpty());
    }
}
