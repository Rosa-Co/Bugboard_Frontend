package com.unina.bugboardapp.controller;

import com.unina.bugboardapp.StartApplication;
import com.unina.bugboardapp.dialog.ErrorDialog;
import com.unina.bugboardapp.dialog.InfoDialog;
import com.unina.bugboardapp.dialog.WarningDialog;
import com.unina.bugboardapp.model.UserType;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import java.util.logging.Logger;

import java.io.IOException;
import java.util.regex.Pattern;

public class UserCreateController {
    private static final Logger logger = Logger.getLogger(UserCreateController.class.getName());
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[a-zA-Z0-9_+&*-]++(?:\\.[a-zA-Z0-9_+&*-]++)*+@(?:[a-zA-Z0-9-]++\\.)++[a-zA-Z]{2,7}$");

    private static final int MIN_PASSWORD_LENGTH = 3;
    private static final String ERROR_MESSAGE = "Validation Error";
    private static final String GENERIC_ERROR = "Error";

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private ComboBox<UserType> typeCombo;

    @FXML
    public void initialize() {
        typeCombo.setItems(FXCollections.observableArrayList(UserType.values()));
        typeCombo.getSelectionModel().select(UserType.USER);
    }

    @FXML
    void onCancel(ActionEvent event) {
        closeWindow();
    }

    @FXML
    void onSave(ActionEvent event) {
        try {
            ValidationResult validation = validateInput();
            if (!validation.valid()) {
                new WarningDialog(ERROR_MESSAGE, validation.errorMessage());
                return;
            }

            createUserAndShowSuccess();
            closeWindow();
        } catch (IllegalArgumentException e) {
            new ErrorDialog(GENERIC_ERROR, e.getMessage());
        }
    }

    private ValidationResult validateInput() {
        String email = emailField.getText();
        String password = passwordField.getText();

        if (email.isBlank() || password.isBlank()) {
            return ValidationResult.error("Please fill in all fields.");
        }

        if (password.length() < MIN_PASSWORD_LENGTH) {
            return ValidationResult.error("Password too short!");
        }

        if (!EMAIL_PATTERN.matcher(email).matches()) {
            return ValidationResult.error("Invalid email format!");
        }
        if(AppController.getInstance().existsUser(email))
            return ValidationResult.error("User already exists!");


        return ValidationResult.success();
    }

    private void createUserAndShowSuccess() {
        AppController.getInstance().createUser(
                emailField.getText(),
                passwordField.getText(),
                typeCombo.getValue());
        new InfoDialog("Success", "User created successfully.");
    }

    private void closeWindow() {
        try {
            StackPane contentArea = (StackPane) emailField.getScene().lookup("#contentArea");

            if (contentArea != null) {
                FXMLLoader loader = new FXMLLoader(StartApplication.class.getResource("issue-list-view.fxml"));
                Node view = loader.load();
                contentArea.getChildren().setAll(view);
            } else {
                Stage stage = (Stage) emailField.getScene().getWindow();
                stage.close();
            }
        } catch (IOException e) {
            logger.warning("Failed to return to the dashboard.");
            new ErrorDialog(GENERIC_ERROR, "Could not return to the dashboard.");
        }
    }

    private record ValidationResult(boolean valid, String errorMessage) {

        static ValidationResult success() {
            return new ValidationResult(true, null);
        }

        static ValidationResult error(String message) {
            return new ValidationResult(false, message);
        }
    }
}