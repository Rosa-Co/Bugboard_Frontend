package com.unina.bugboardapp.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class User {

    private Integer id;
    @JsonProperty("email")
    private String username;
    @JsonProperty("password")
    private final String password;
    @JsonProperty("role")
    private final UserType type;

    public User() {
        this.username = "";
        this.password = "";
        this.type = UserType.USER;
    }

    @JsonCreator
    public User(@JsonProperty("email") String username,
            @JsonProperty("password") String password,
            @JsonProperty("role") UserType type) {
        this.username = username;
        this.password = password;
        this.type = type != null ? type : UserType.USER;
    }

    @JsonProperty("email")
    public String getUsername() {
        return username;
    }

    public boolean checkPassword(String password) {
        return this.password.equals(password);
    }

    @JsonProperty("role")
    public UserType getType() {
        return type;
    }

    public String getPassword() {
        return password;
    }// ?non so se si può fare...

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        User user = (User) o;
        return Objects.equals(username, user.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username);
    }
}
