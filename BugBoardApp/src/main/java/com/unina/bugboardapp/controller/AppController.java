package com.unina.bugboardapp.controller;

import com.unina.bugboardapp.model.Comment;
import com.unina.bugboardapp.model.*;
import com.unina.bugboardapp.service.AuthService;
import com.unina.bugboardapp.service.CommentService;
import com.unina.bugboardapp.service.IssueService;
import com.unina.bugboardapp.service.UserService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Main application controller (Singleton)
 * Manages users, issues, authentication and application state
 */
public class AppController {

    private static AppController instance;
    private final ObservableList<User> users;
    private final ObservableList<Issue> issues;

    private final AuthService authService;
    private final IssueService issueService;
    private final UserService userService;
    private final CommentService commentService;

    private User loggedUser;

    private AppController() {
        users = FXCollections.observableArrayList();
        issues = FXCollections.observableArrayList();

        this.authService = new AuthService();
        this.issueService = new IssueService();
        this.userService = new UserService();
        this.commentService = new CommentService();
    }

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
            User user = authService.login(email, password);

            if (user != null) {
                this.loggedUser = user;
                refreshData();
                System.out.println("User logged in: " + loggedUser.getUsername() + " (" + loggedUser.getType() + ")");
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.err.println("Login failed: Invalid email or password");
        return false;
    }

    public void refreshData() {
        new Thread(() -> {
            try {
                List<Issue> realIssues = issueService.fetchAllIssues();

                javafx.application.Platform.runLater(() -> {
                    issues.clear();
                    if (realIssues != null) {
                        issues.addAll(realIssues);
                    }
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
        return loggedUser != null && loggedUser.getType() == UserType.ADMIN;
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
    public void createUser(String email, String password, UserType type) {
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

        User newUser = new User(normalizedEmail, password, type);
        new Thread(() -> {
            try {
                User createdUser = userService.createUser(newUser);
                javafx.application.Platform.runLater(() -> {
                    if (createdUser != null) {
                        users.add(createdUser);
                        System.out.println("User creato su server e UI");
                    }
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
    public void createIssue(String title, String description, IssueType type,
            Priority priority, String imagePath, IssueState state) {
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

        Issue newIssue = new Issue(type, title, description, null, state, priority, loggedUser);
        if (imagePath != null && !imagePath.trim().isEmpty()) {
            newIssue.setImagePath(imagePath.trim());
        }
        newIssue.setCreatedAt(null);

        new Thread(() -> {
            try {
                Issue createdIssue = issueService.createIssue(newIssue);
                javafx.application.Platform.runLater(() -> {
                    if (createdIssue != null) {
                        issues.add(createdIssue);
                        System.out.println("Issue creata su server e UI");
                    }
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
    public void addComment(Issue issue, String content, Consumer<Comment> onSuccess) {
        if (!isLoggedIn()) {
            throw new IllegalStateException("User must be logged in to add comments");
        }

        if (issue == null) {
            throw new IllegalArgumentException("Issue cannot be null");
        }

        if (content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("Comment content cannot be empty");
        }

        Comment newComment = new Comment(loggedUser, content.trim(), issue.getId());
        new Thread(() -> {
            try {
                Comment createdComment = commentService.createComment(newComment);
                javafx.application.Platform.runLater(() -> {
                    if (createdComment != null) {
                        issue.addComment(createdComment);
                        System.out.println("Commento creato su server e UI");
                        if (onSuccess != null)
                            onSuccess.accept(createdComment);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void loadCommentsForIssue(Issue issue) {
        new Thread(() -> {
            try {
                List<Comment> comments = commentService.getCommentsByIssueId(issue.getId());
                javafx.application.Platform.runLater(() -> {
                    if (comments != null) {
                        issue.setComments(comments);
                    }
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
    public ObservableList<Issue> getIssuesFiltered(IssueType type) {
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
    public ObservableList<Issue> getIssuesByPriority(Priority priority) {
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
    public ObservableList<Issue> getIssuesByState(IssueState state) {
        if (state == null) {
            return FXCollections.observableArrayList(issues);
        }

        return issues.stream()
                .filter(i -> i.getState() == state)
                .collect(Collectors.toCollection(FXCollections::observableArrayList));
    }
}