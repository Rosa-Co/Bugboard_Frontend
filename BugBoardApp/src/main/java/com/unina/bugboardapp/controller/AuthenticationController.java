package com.unina.bugboardapp.controller;

import com.unina.bugboardapp.exception.AuthenticationException;
import com.unina.bugboardapp.model.User;
import com.unina.bugboardapp.service.AuthService;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AuthenticationController {
    private static final Logger logger = Logger.getLogger(AuthenticationController.class.getName());
    private final AuthService authService;
    private final AppState appState;
    private final IssueController issueController;

    public AuthenticationController(AppState appState, IssueController issueController) {
        this.appState = appState;
        this.issueController = issueController;
        this.authService = new AuthService();
    }

    public boolean login(String email, String password) {
        if (email == null || email.trim().isEmpty() || password == null || password.isEmpty()) {
            logger.warning("Login failed: Invalid credentials provided");
            return false;
        }

        try {
            User user = authService.login(email, password);

            if (user != null) {
                appState.setLoggedUser(user);
                issueController.refreshData(); // Scarica i dati dopo il login
                logger.info("User logged in: " + user.getUsername() + " (" + user.getType() + ")");
                return true;
            }
        } catch (AuthenticationException e) {
            logger.log(Level.SEVERE, "Errore durante il login", e);
        } catch (Exception e1) {
            logger.log(Level.SEVERE, "Errore inaspettato durante il login", e1);
        }
        logger.warning("Login failed: Invalid email or password");
        return false;
    }

    public void logout() {
        if (appState.getLoggedUser() != null) {
            logger.info("User logged out: " + appState.getLoggedUser().getUsername());
            appState.setLoggedUser(null);
        }
    }
}