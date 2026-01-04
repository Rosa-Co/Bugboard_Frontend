package com.unina.bugboardapp.controller;

import com.unina.bugboardapp.exception.*;
import com.unina.bugboardapp.model.Comment;
import com.unina.bugboardapp.model.*;
import com.unina.bugboardapp.service.AuthService;
import com.unina.bugboardapp.service.CommentService;
import com.unina.bugboardapp.service.IssueService;
import com.unina.bugboardapp.service.UserService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class AppController {

    private static AppController instance;
    private final ObservableList<User> users;
    private final ObservableList<Issue> issues;
    private static final Logger logger = Logger.getLogger(AppController.class.getName());

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
            logger.warning("Login failed: Invalid credentials provided");
            return false;
        }

        try {
            User user = authService.login(email, password);

            if (user != null) {
                this.loggedUser = user;
                refreshData();
                logger.info("User logged in: " + loggedUser.getUsername() + " (" + loggedUser.getType() + ")");
                return true;
            }
        } catch (AuthenticationException e) {
            Thread.currentThread().interrupt();
            logger.log(Level.SEVERE,"Errore durante il login" , e);
        } catch(Exception e1){
            Thread.currentThread().interrupt();
            logger.log(Level.SEVERE,"Errore inaspettato durante il login" , e1);
        }
        logger.warning("Login failed: Invalid email or password");
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
                    logger.info("Dati aggiornati dal backend!");
                });

            } catch (IssueException e) {
                logger.warning("Impossibile scaricare le issue: " + e.getMessage());
            } catch (Exception e1) {
                Thread.currentThread().interrupt();
                logger.warning("Errore inaspettato durante il download dei dati: " + e1.getMessage());
            }
        }).start();
    }

    public void logout() {
        if (loggedUser != null) {
            logger.info("User logged out: " + loggedUser.getUsername());
            this.loggedUser = null;
        }
    }

    public User getLoggedUser() {
        return loggedUser;
    }

    public boolean isLoggedIn() {
        return loggedUser != null;
    }

    public boolean isCurrentUserAdmin() {
        return loggedUser != null && loggedUser.getType() == UserType.ADMIN;
    }

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
                        logger.info("User creato su server e UI");
                    }
                });
            } catch (UserException e) {
                logger.warning("Errore durante la creazione dell'utente: " + e.getMessage());
            } catch (Exception e1) {
                Thread.currentThread().interrupt();
                logger.warning("Errore inaspettato durante la creazione dell'utente: " + e1.getMessage());
            }
        }).start();
    }

    public void createIssue(String title, String description, IssueType type,
                            Priority priority, String imagePath, IssueState state) {
        validateUserAndInput(title, description, type, priority, state);

        Issue newIssue = buildIssue(title, description, type, priority, state, imagePath);

        createIssueAsync(newIssue);
    }

    private void validateUserAndInput(String title, String description,
                                      IssueType type, Priority priority, IssueState state) {
        if (!isLoggedIn()) {
            throw new IllegalStateException("User must be logged in to create issues");
        }
        validateTitle(title);
        validateDescription(description);
        validateEnums(type, priority, state);
    }

    private void validateTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Issue title cannot be empty");
        }
    }

    private void validateDescription(String description) {
        if (description == null || description.trim().isEmpty()) {
            throw new IllegalArgumentException("Issue description cannot be empty");
        }
    }

    private void validateEnums(IssueType type, Priority priority, IssueState state) {
        if (type == null || priority == null || state == null) {
            throw new IllegalArgumentException("Issue type, state and priority cannot be null");
        }
    }

    private Issue buildIssue(String title, String description, IssueType type,
                             Priority priority, IssueState state, String imagePath) {
        Issue newIssue = new Issue(type, title, description, null, state, priority, loggedUser);

        if (imagePath != null && !imagePath.trim().isEmpty()) {
            newIssue.setImagePath(imagePath.trim());
        }
        return newIssue;
    }

    private void createIssueAsync(Issue newIssue) {
        new Thread(() -> {
            try {
                Issue createdIssue = issueService.createIssue(newIssue);
                updateUIWithCreatedIssue(createdIssue);
            } catch (IssueException e) {
                logger.log(Level.SEVERE, "Errore durante la creazione della Issue", e);
            } catch (Exception e1) {
                Thread.currentThread().interrupt();
                logger.log(Level.SEVERE, "Errore inaspettato durante la creazione della Issue", e1);
            }
        }).start();
    }

    private void updateUIWithCreatedIssue(Issue createdIssue) {
        javafx.application.Platform.runLater(() -> {
            if (createdIssue != null) {
                issues.add(createdIssue);
                logger.info("Issue creata su server e UI");
            }
        });
    }

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
                        logger.info("Commento creato su server e UI");
                        if (onSuccess != null)
                            onSuccess.accept(createdComment);
                    }
                });
            } catch (CommentException e) {
                logger.log(Level.SEVERE,"Errore durante la creazione del commento" , e);
            } catch (Exception e1) {
                Thread.currentThread().interrupt();
                logger.log(Level.SEVERE,"Errore inaspettato durante la creazione del commento" , e1);
            }
        }).start();
    }

    public boolean existsUser(String email){
        try {
            return userService.existsUser(email);
        }catch(ApiException e){
            if(e.getStatusCode()==404) return false;
            throw e;
        }catch(IOException e1){
            logger.log(Level.SEVERE,"Errore durante la verifica dell'esistenza dell'utente: il server non ha risposto" , e1);
            return true;
        }catch(InterruptedException e2){
            Thread.currentThread().interrupt();
            logger.log(Level.SEVERE,"Errore durante la verifica dell'esistenza dell'utente: errore nella GET" , e2);
            return true;
        }catch(Exception e3){
            logger.log(Level.SEVERE,"Errore durante la verifica dell'esistenza dell'utente: errore generico" , e3);
            return true;
        }

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
            } catch (CommentException e) {
                logger.log(Level.SEVERE,"Errore durante il caricamento dei commenti" , e);
            } catch (Exception e1) {
                Thread.currentThread().interrupt();
                logger.log(Level.SEVERE,"Errore inaspettato durante il caricamento dei commenti" , e1);
            }
        }).start();
    }

    public ObservableList<Issue> getAllIssues() {
        return issues;
    }

    public ObservableList<User> getAllUsers() {
        return users;
    }

    public ObservableList<Issue> getIssuesFiltered(IssueType type) {
        if (type == null) {
            return FXCollections.observableArrayList(issues);
        }

        return issues.stream()
                .filter(i -> i.getType() == type)
                .collect(Collectors.toCollection(FXCollections::observableArrayList));
    }

    public ObservableList<Issue> getIssuesByPriority(Priority priority) {
        if (priority == null) {
            return FXCollections.observableArrayList(issues);
        }

        return issues.stream()
                .filter(i -> i.getPriority() == priority)
                .collect(Collectors.toCollection(FXCollections::observableArrayList));
    }

    public ObservableList<Issue> getIssuesByState(IssueState state) {
        if (state == null) {
            return FXCollections.observableArrayList(issues);
        }

        return issues.stream()
                .filter(i -> i.getState() == state)
                .collect(Collectors.toCollection(FXCollections::observableArrayList));
    }
}