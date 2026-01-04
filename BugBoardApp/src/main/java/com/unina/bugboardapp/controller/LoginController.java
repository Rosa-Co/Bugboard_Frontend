package com.unina.bugboardapp.controller;

import com.unina.bugboardapp.StartApplication;
import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.util.logging.Level;
import java.util.logging.Logger;

import java.io.IOException;
import java.util.regex.Pattern;


public class LoginController {
    private static final Logger logger = Logger.getLogger(LoginController.class.getName());
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[a-zA-Z0-9_+&*-]++(?:\\.[a-zA-Z0-9_+&*-]++)*+@(?:[a-zA-Z0-9-]++\\.)++[a-zA-Z]{2,7}$");

    private static final int MIN_PASSWORD_LENGTH = 3;
    private static final String DASHBOARD_VIEW = "dashboard-view.fxml";
    private static final String DASHBOARD_TITLE = "BugBoard - Dashboard";

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label errorLabel;

    @FXML
    private Button loginButton;


    @FXML
    public void initialize() {
        // Hide error label initially
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);

        // Add Enter key support
        passwordField.setOnAction(this::onLogin);

        emailField.textProperty().addListener((obs, oldVal, newVal) -> hideError());
        passwordField.textProperty().addListener((obs, oldVal, newVal) -> hideError());
    }

    @FXML
    void onLogin(ActionEvent event) {
        String email = emailField.getText().trim();
        String password = passwordField.getText();

        loginButton.setDisable(true);

        if (!validateInputs(email, password)) {
            loginButton.setDisable(false);
            return;
        }

        boolean success = AppController.getInstance().login(email, password);

        if (success) {
            hideError();
            navigateToDashboard(event);
        } else {
            showError("Invalid email or password. Please try again.");
            loginButton.setDisable(false);
        }
    }

    private boolean validateInputs(String email, String password) {
        if (email.isEmpty() && password.isEmpty()) {
            showError("Please enter your email and password.");
            return false;
        }

        if (email.isEmpty()) {
            showError("Please enter your email address.");
            emailField.requestFocus();
            return false;
        }

        if (password.isEmpty()) {
            showError("Please enter your password.");
            passwordField.requestFocus();
            return false;
        }

        if (!isValidEmail(email)) {
            showError("Please enter a valid email address.");
            emailField.requestFocus();
            return false;
        }

        if (password.length() < MIN_PASSWORD_LENGTH) {
            showError("Password must be at least " + MIN_PASSWORD_LENGTH + " characters long.");
            passwordField.requestFocus();
            return false;
        }

        return true;
    }

    private boolean isValidEmail(String email) {
        return email != null
                && EMAIL_PATTERN.matcher(email).matches()
                && email.length() <= 254;
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
        errorLabel.setManaged(true);

        FadeTransition fade = new FadeTransition(Duration.millis(300), errorLabel);
        fade.setFromValue(0.0);
        fade.setToValue(1.0);
        fade.play();
    }

    private void hideError() {
        if (errorLabel.isVisible()) {
            FadeTransition fade = new FadeTransition(Duration.millis(200), errorLabel);
            fade.setFromValue(1.0);
            fade.setToValue(0.0);
            fade.setOnFinished(e -> {
                errorLabel.setVisible(false);
                errorLabel.setManaged(false);
            });
            fade.play();
        }
    }

    private void navigateToDashboard(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(StartApplication.class.getResource(DASHBOARD_VIEW));
            Parent root = loader.load();

            Scene currentScene = ((Node) event.getSource()).getScene();
            Stage stage = (Stage) currentScene.getWindow();

            Scene newScene = new Scene(root);

            FadeTransition fadeOut = createFadeTransition(currentScene, stage, newScene);
            fadeOut.play();

        } catch (IOException e) {
            logger.log(Level.SEVERE,"Failed to load dashboard" , e);
            showError("Unable to load dashboard. Please try again.");
            loginButton.setDisable(false);
        }
    }
    private FadeTransition createFadeTransition(Scene currentScene, Stage stage, Scene newScene) {
        FadeTransition fadeOut = new FadeTransition(Duration.millis(300), currentScene.getRoot());
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        fadeOut.setOnFinished(e -> {
            stage.setScene(newScene);
            stage.setTitle(DASHBOARD_TITLE);
            stage.centerOnScreen();

            FadeTransition fadeIn = new FadeTransition(Duration.millis(300), newScene.getRoot());
            fadeIn.setFromValue(0.0);
            fadeIn.setToValue(1.0);
            fadeIn.play();
        });
        return fadeOut;
    }
}
