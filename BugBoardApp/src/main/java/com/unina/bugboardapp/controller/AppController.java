package com.unina.bugboardapp.controller;

import com.unina.bugboardapp.model.*;
import javafx.collections.ObservableList;

import java.util.function.Consumer;

public class AppController {

    private static AppController instance;
    private final AppState appState;
    private final AuthenticationController authController;
    private final IssueController issueController;
    private final UserController userController;
    private final CommentController commentController;

    private AppController() {
        this.appState = new AppState();
        this.issueController = new IssueController(appState);
        this.authController = new AuthenticationController(appState, issueController); // Auth triggera refresh issue
        this.userController = new UserController(appState);
        this.commentController = new CommentController(appState);
    }

    public static synchronized AppController getInstance() {
        if (instance == null) {
            instance = new AppController();
        }
        return instance;
    }

    public boolean login(String email, String password) {
        return authController.login(email, password);
    }

    public void logout() {
        authController.logout();
    }

    public User getLoggedUser() {
        return appState.getLoggedUser();
    }

    public boolean isLoggedIn() {
        return appState.isLoggedIn();
    }

    public boolean isCurrentUserAdmin() {
        return appState.isCurrentUserAdmin();
    }

    public void refreshData() {
        issueController.refreshData();
    }

    public void createIssue(String title, String description, IssueType type,
                            Priority priority, String imagePath, IssueState state) {
        issueController.createIssue(title, description, type, priority, imagePath, state);
    }

    public ObservableList<Issue> getAllIssues() {
        return appState.getIssues();
    }

    public ObservableList<Issue> getIssuesFiltered(IssueType type) {
        return issueController.getIssuesFiltered(type);
    }

    public ObservableList<Issue> getIssuesByPriority(Priority priority) {
        return issueController.getIssuesByPriority(priority);
    }

    public ObservableList<Issue> getIssuesByState(IssueState state) {
        return issueController.getIssuesByState(state);
    }

    public void createUser(String email, String password, UserType type) {
        userController.createUser(email, password, type);
    }

    public boolean existsUser(String email) {
        return userController.existsUser(email);
    }

    public ObservableList<User> getAllUsers() {
        return appState.getUsers();
    }


    public void addComment(Issue issue, String content, Consumer<Comment> onSuccess) {
        commentController.addComment(issue, content, onSuccess);
    }

    public void loadCommentsForIssue(Issue issue) {
        commentController.loadCommentsForIssue(issue);
    }
}