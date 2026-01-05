package com.unina.bugboardapp.controller;

import com.unina.bugboardapp.exception.ApiException;
import com.unina.bugboardapp.exception.UserException;
import com.unina.bugboardapp.model.User;
import com.unina.bugboardapp.model.enums.UserType;
import com.unina.bugboardapp.service.UserService;
import javafx.application.Platform;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UserController {
    private static final Logger logger = Logger.getLogger(UserController.class.getName());
    private final UserService userService;
    private final AppState appState;

    public UserController(AppState appState) {
        this.appState = appState;
        this.userService = new UserService();
    }

    public void createUser(String email, String password, UserType type) {
        if (!appState.isCurrentUserAdmin()) {
            throw new IllegalStateException("Only administrators can create users");
        }
        validateUserInput(email, password, type);

        String normalizedEmail = email.trim().toLowerCase();
        if (appState.getUsers().stream().anyMatch(u -> u.getUsername().equalsIgnoreCase(normalizedEmail))) {
            throw new IllegalArgumentException("User with this email already exists");
        }

        User newUser = new User(normalizedEmail, password, type);
        new Thread(() -> {
            try {
                User createdUser = userService.createUser(newUser);
                Platform.runLater(() -> {
                    if (createdUser != null) {
                        appState.getUsers().add(createdUser);
                        logger.info("User creato su server e UI");
                    }
                });
            } catch (UserException e) {
                logger.warning("Errore durante la creazione dell'utente: " + e.getMessage());
            } catch (Exception e1) {
                logger.warning("Errore inaspettato durante la creazione dell'utente: " + e1.getMessage());
            }
        }).start();
    }

    private void validateUserInput(String email, String password, UserType type) {
        if (email == null || email.trim().isEmpty()) throw new IllegalArgumentException("Email cannot be empty");
        if (password == null || password.isEmpty()) throw new IllegalArgumentException("Password cannot be empty");
        if (type == null) throw new IllegalArgumentException("User type cannot be null");
    }

    public boolean existsUser(String email) {
        try {
            return userService.existsUser(email);
        } catch (ApiException e) {
            if (e.getStatusCode() == 404) return false;
            throw e;
        } catch (IOException e1) {
            logger.log(Level.SEVERE, "Errore durante la verifica dell'esistenza dell'utente: il server non ha risposto", e1);
            return true; // Fail-safe
        } catch (InterruptedException e2) {
            Thread.currentThread().interrupt();
            logger.log(Level.SEVERE, "Errore durante la verifica dell'esistenza dell'utente: errore nella GET", e2);
            return true;
        } catch (Exception e3) {
            logger.log(Level.SEVERE, "Errore durante la verifica dell'esistenza dell'utente: errore generico", e3);
            return true;
        }
    }
}