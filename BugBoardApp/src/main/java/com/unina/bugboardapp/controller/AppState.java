package com.unina.bugboardapp.controller;

import com.unina.bugboardapp.model.Issue;
import com.unina.bugboardapp.model.User;
import com.unina.bugboardapp.model.enums.UserType;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class AppState {
    private final ObservableList<User> users;
    private final ObservableList<Issue> issues;
    private User loggedUser;

    public AppState() {
        this.users = FXCollections.observableArrayList();
        this.issues = FXCollections.observableArrayList();
    }

    public ObservableList<User> getUsers() {
        return users;
    }

    public ObservableList<Issue> getIssues() {
        return issues;
    }

    public User getLoggedUser() {
        return loggedUser;
    }

    public void setLoggedUser(User loggedUser) {
        this.loggedUser = loggedUser;
    }

    public boolean isLoggedIn() {
        return loggedUser != null;
    }

    public boolean isCurrentUserAdmin() {
        return loggedUser != null && loggedUser.getType() == UserType.ADMIN;
    }
}