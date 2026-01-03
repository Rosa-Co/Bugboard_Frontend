package com.unina.bugboardapp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.unina.bugboardapp.model.UserType;

public class UserCreateRequest {
    @JsonProperty("email")
    private String email;

    @JsonProperty("password")
    private String password;

    @JsonProperty("role")
    private UserType role;

    public UserCreateRequest() {
    }

    public UserCreateRequest(String email, String password, UserType role) {
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public UserType getRole() {
        return role;
    }

    public void setRole(UserType role) {
        this.role = role;
    }
}
