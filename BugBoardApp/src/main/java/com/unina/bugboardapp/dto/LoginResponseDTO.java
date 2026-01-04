package com.unina.bugboardapp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class LoginResponseDTO {
    private String token;
    @JsonProperty("id")
    private Integer userId;
    private String type;
    private String email;
    private List<String> roles;

    public LoginResponseDTO() {
        /*costruttore vuoto per la costruzione del messaggio JSON*/
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public String getRole() {
        if (roles != null && !roles.isEmpty() && roles.getFirst().contains("ADMIN")) {
            return "ADMIN";
        }
        return "USER";
    }
}
