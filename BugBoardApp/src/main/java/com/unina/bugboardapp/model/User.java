package com.unina.bugboardapp.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class User {

    public enum UserType {
        ADMIN,
        NORMAL
    }

    private Integer id;
    private final String username; // treating email as username/id
    private final String password;
    private final UserType type;

    public User(@JsonProperty("email") String username,@JsonProperty("password") String password,@JsonProperty("roles") String type) {
        this.username = username;
        this.password = password;
        this.type = switch(type.toUpperCase()){
            case "ADMIN" -> UserType.ADMIN;
            default -> UserType.NORMAL;
        };
    }

    @JsonProperty("email")
    public String getUsername() {
        return username;
    }
    @JsonProperty("isAdmin")
    public boolean isAdmin() {
        return type == UserType.ADMIN;
    }
    public boolean checkPassword(String password) {
        // TODO : implement proper password hashing
        return this.password.equals(password);
    }
    @JsonIgnore
    public UserType getType() {
        return type;
    }

    public String getPassword() {
        return password;
    }//?non so se si può fare...

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
