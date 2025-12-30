package com.unina.bugboardapp.controller;

import com.unina.bugboardapp.model.Comment;
import com.unina.bugboardapp.model.Issue;
import com.unina.bugboardapp.model.User;
import com.unina.bugboardapp.service.BackendService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Main application controller (Singleton)
 * Manages users, issues, authentication and application state
 */
public class AppController {

    private static AppController instance;

    // Observable collections for reactive UI updates
    private final ObservableList<User> users;
    private final BackendService backendService;
    private final ObservableList<Issue> issues;
    private User loggedUser;

    /**
     * Private constructor to enforce singleton pattern
     */
    private AppController() {
        users = FXCollections.observableArrayList();
        issues = FXCollections.observableArrayList();
        //initMockData();
        backendService= new BackendService();
    }

    /**
     * Returns the singleton instance of AppController
     * Thread-safe implementation
     * 
     * @return The AppController instance
     */
    public static synchronized AppController getInstance() {
        if (instance == null) {
            instance = new AppController();
        }
        return instance;
    }

    public boolean login(String email, String password) {
        if (email == null || email.trim().isEmpty() || password == null || password.isEmpty()) {
            System.err.println("Login failed: Invalid credentials provided");
            return false;
        }

        try {
            User user = backendService.login(email,password);

            if (user!=null) {
                this.loggedUser = user;
                refreshData();
                System.out.println("User logged in: " + loggedUser.getUsername() + " (" + loggedUser.getType() + ")");
                return true;
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        System.err.println("Login failed: Invalid email or password");
        return false;
    }

    public void refreshData() {
        new Thread(() -> {
            try {
                List<Issue> realIssues = backendService.fetchAllIssues();

                javafx.application.Platform.runLater(() -> {
                    issues.clear();
                    issues.addAll(realIssues);
                    System.out.println("Dati aggiornati dal backend!");
                });

            } catch (Exception e) {
                System.err.println("Impossibile scaricare le issue: " + e.getMessage());
            }
        }).start();
    }

    /**
     * Logs out the current user
     */
    public void logout() {
        if (loggedUser != null) {
            System.out.println("User logged out: " + loggedUser.getUsername());
            this.loggedUser = null;
        }
    }

    /**
     * Returns the currently logged-in user
     * 
     * @return Current user or null if not logged in
     */
    public User getLoggedUser() {
        return loggedUser;
    }

    /**
     * Checks if a user is currently logged in
     * 
     * @return true if user is logged in, false otherwise
     */
    public boolean isLoggedIn() {
        return loggedUser != null;
    }

    /**
     * Checks if the current user is an admin
     * 
     * @return true if current user is admin, false otherwise
     */
    public boolean isCurrentUserAdmin() {
        return loggedUser != null && loggedUser.getType() == User.UserType.ADMIN;
    }

    /**
     * Creates a new user
     * 
     * @param email    User's email address
     * @param password User's password
     * @param type     User type (ADMIN or NORMAL)
     * @throws IllegalArgumentException if user already exists or invalid parameters
     * @throws IllegalStateException    if current user is not an admin
     */
    public void createUser(String email, String password, User.UserType type) {
        if (!isCurrentUserAdmin()) {
            throw new IllegalStateException("Only administrators can create users");
        }

        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be empty");
        }

        if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException("Password cannot be empty");
        }

        if (type == null) {
            throw new IllegalArgumentException("User type cannot be null");
        }

        String normalizedEmail = email.trim().toLowerCase();

        if (users.stream().anyMatch(u -> u.getUsername().equalsIgnoreCase(normalizedEmail))) {
            throw new IllegalArgumentException("User with this email already exists");
        }

        //users.add(new User(normalizedEmail, password, type));
        //System.out.println("New user created: " + normalizedEmail + " (" + type + ")");
        boolean isAdmin= type == User.UserType.ADMIN;
        User newUser= new User(normalizedEmail,password,isAdmin);
        new Thread(() -> {
            try {
                backendService.createUser(newUser);
                javafx.application.Platform.runLater(() -> {
                    users.add(newUser);
                    System.out.println("User creato su server e UI");
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    /**
     * Creates a new issue
     * 
     * @param title       Issue title
     * @param description Issue description
     * @param type        Issue type
     * @param priority    Issue priority
     * @param imagePath   Optional image path
     * @throws IllegalStateException    if user is not logged in
     * @throws IllegalArgumentException if invalid parameters
     */
    public void createIssue(String title, String description, Issue.IssueType type,
            Issue.Priority priority, String imagePath, Issue.IssueState state) {
        if (!isLoggedIn()) {
            throw new IllegalStateException("User must be logged in to create issues");
        }

        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Issue title cannot be empty");
        }

        if (description == null || description.trim().isEmpty()) {
            throw new IllegalArgumentException("Issue description cannot be empty");
        }

        if (type == null || priority == null || state == null) {
            throw new IllegalArgumentException("Issue type, state and priority cannot be null");
        }

        Issue newIssue = new Issue(type.name(),title, description,null,state.name(),priority.name(), loggedUser);
        if (imagePath != null && !imagePath.trim().isEmpty()) {
            newIssue.setImagePath(imagePath.trim());
        }
        newIssue.setCreatedAt(null);

        new Thread(() -> {
            try {
                Issue createdIssue= backendService.createIssue(newIssue);
                javafx.application.Platform.runLater(() -> {
                    issues.add(createdIssue);
                    System.out.println("Issue creata su server e UI");
                });
            } catch (Exception e) {
                System.err.println("Errore durante la creazione della Issue: " + e.getMessage());
                e.printStackTrace();
            }
        }).start();

    }

    /**
     * Adds a comment to an issue
     * 
     * @param issue   The issue to comment on
     * @param content The comment content
     * @throws IllegalStateException    if user is not logged in
     * @throws IllegalArgumentException if invalid parameters
     */
    public void addComment(Issue issue, String content) {
        if (!isLoggedIn()) {
            throw new IllegalStateException("User must be logged in to add comments");
        }

        if (issue == null) {
            throw new IllegalArgumentException("Issue cannot be null");
        }

        if (content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("Comment content cannot be empty");
        }

        //issue.addComment(new Comment(loggedUser, content.trim()));
        //System.out.println("Comment added to issue #" + issue.getId());
        Comment newComment= new Comment(loggedUser,content.trim(),issue);
        new Thread(() -> {
            try {
                Comment createdComment=backendService.createComment(newComment);
                javafx.application.Platform.runLater(() -> {
                    issue.addComment(createdComment);
                    System.out.println("Commento creato su server e UI");
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    /**
     * Returns all issues
     * 
     * @return Observable list of all issues
     */
    public ObservableList<Issue> getAllIssues() {
        return issues;
    }

    /**
     * Returns all users
     * 
     * @return Observable list of all users
     */
    public ObservableList<User> getAllUsers() {
        return users;
    }

    /**
     * Filters issues by type
     * 
     * @param type Issue type to filter by (null returns all issues)
     * @return Filtered observable list of issues
     */
    public ObservableList<Issue> getIssuesFiltered(Issue.IssueType type) {
        if (type == null) {
            return FXCollections.observableArrayList(issues);
        }

        return issues.stream()
                .filter(i -> i.getType() == type)
                .collect(Collectors.toCollection(FXCollections::observableArrayList));
    }

    /**
     * Filters issues by priority
     * 
     * @param priority Priority to filter by
     * @return Filtered observable list of issues
     */
    public ObservableList<Issue> getIssuesByPriority(Issue.Priority priority) {
        if (priority == null) {
            return FXCollections.observableArrayList(issues);
        }

        return issues.stream()
                .filter(i -> i.getPriority() == priority)
                .collect(Collectors.toCollection(FXCollections::observableArrayList));
    }

    /**
     * Filters issues by state
     * 
     * @param state State to filter by
     * @return Filtered observable list of issues
     */
    public ObservableList<Issue> getIssuesByState(Issue.IssueState state) {
        if (state == null) {
            return FXCollections.observableArrayList(issues);
        }

        return issues.stream()
                .filter(i -> i.getState() == state)
                .collect(Collectors.toCollection(FXCollections::observableArrayList));
    }
}