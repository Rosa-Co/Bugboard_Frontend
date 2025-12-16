package com.unina.bugboardapp.model;

import java.util.Objects;

public class User {

    public enum UserType {
        ADMIN,
        NORMAL
    }

    private final String username; // treating email as username/id
    private final String password;
    private final UserType type;

    public User(String username, String password, UserType type) {
        this.username = username;
        this.password = password;
        this.type = type;
    }

    public String getUsername() {
        return username;
    }

    public boolean checkPassword(String password) {
        // TODO : implement proper password hashing
        return this.password.equals(password);
    }

    public UserType getType() {
        return type;
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
