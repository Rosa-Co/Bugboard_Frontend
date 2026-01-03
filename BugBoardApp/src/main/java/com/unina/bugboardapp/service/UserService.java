package com.unina.bugboardapp.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.unina.bugboardapp.model.User;
import com.unina.bugboardapp.dto.UserCreateRequest;

public class UserService {

    private final ApiClient apiClient;
    private final ObjectMapper mapper;

    public UserService() {
        this.apiClient = ApiClient.getInstance();
        this.mapper = new ObjectMapper();
        this.mapper.registerModule(new JavaTimeModule());
        this.mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    public User createUser(User newUser) throws Exception {
        UserCreateRequest request = new UserCreateRequest(newUser.getUsername(), newUser.getPassword(),
                newUser.getType());
        String requestBody = mapper.writeValueAsString(request);
        String responseBody = apiClient.post("/users", requestBody);

        if (responseBody != null && !responseBody.isEmpty()) {
            // Mapping UtenteDTO from backend to User frontend model
            // Backend UtenteDTO has {id, email, role}
            return mapper.readValue(responseBody, User.class);
        }
        return null;
    }
}
