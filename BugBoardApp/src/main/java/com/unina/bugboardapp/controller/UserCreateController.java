package com.unina.bugboardapp.controller;

import com.unina.bugboardapp.StartApplication;
import com.unina.bugboardapp.model.User;
import com.unina.bugboardapp.model.UserType;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.regex.Pattern;

public class UserCreateController {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$");

    private static final int MIN_PASSWORD_LENGTH = 6;

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
                showAlert("Validation Error", validation.errorMessage());
                return;
            }

            createUserAndShowSuccess();
            closeWindow();
        } catch (IllegalArgumentException e) {
            showAlert("Error", e.getMessage());
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

        return ValidationResult.success();
    }

    private void createUserAndShowSuccess() {
        AppController.getInstance().createUser(
                emailField.getText(),
                passwordField.getText(),
                typeCombo.getValue());
        showAlert("Success", "User created successfully.");
    }

    private void closeWindow() {
        try {
            // Otteniamo il contentArea dalla Dashboard risalendo dal nodo corrente
            StackPane contentArea = (StackPane) emailField.getScene().lookup("#contentArea");

            if (contentArea != null) {
                // Carichiamo la vista delle issue
                FXMLLoader loader = new FXMLLoader(StartApplication.class.getResource("issue-list-view.fxml"));
                Node view = loader.load();
                contentArea.getChildren().setAll(view);
            } else {
                // Fallback nel caso la vista sia aperta in una finestra separata (modal)
                Stage stage = (Stage) emailField.getScene().getWindow();
                stage.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Could not return to the dashboard.");
        }
    }

    private void showAlert(String header, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        if (header.contains("Error"))
            alert.setAlertType(Alert.AlertType.ERROR);
        alert.setTitle(header);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
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