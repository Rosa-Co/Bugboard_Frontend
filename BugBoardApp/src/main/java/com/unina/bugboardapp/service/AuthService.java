package com.unina.bugboardapp.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.unina.bugboardapp.dto.LoginRequestDTO;
import com.unina.bugboardapp.dto.LoginResponseDTO;
import com.unina.bugboardapp.manager.SessionManager;
import com.unina.bugboardapp.model.User;
import com.unina.bugboardapp.model.UserType;

public class AuthService {

    private final ApiClient apiClient;
    private final ObjectMapper mapper;

    public AuthService() {
        this.apiClient = ApiClient.getInstance();
        this.mapper = new ObjectMapper();
        this.mapper.registerModule(new JavaTimeModule());
        this.mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    public User login(String email, String password) throws Exception {
        LoginRequestDTO loginRequest = new LoginRequestDTO(email, password);
        String requestBody = mapper.writeValueAsString(loginRequest);

        String responseBody = apiClient.post("/auth/login", requestBody);

        if (responseBody != null && !responseBody.isEmpty()) {
            LoginResponseDTO response = mapper.readValue(responseBody, LoginResponseDTO.class);
            User user = new User(email, password,
                    "ADMIN".equalsIgnoreCase(response.getRole()) ? UserType.ADMIN : UserType.USER);
            user.setId(response.getUserId());
            SessionManager.getInstance().login(user, response.getToken());
            return user;
        }
        return null;
    }
}
