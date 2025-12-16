package com.unina.bugboardapp.controller;

import com.unina.bugboardapp.model.User;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class UserCreateController {

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private ComboBox<User.UserType> typeCombo;

    @FXML
    public void initialize() {
        typeCombo.setItems(FXCollections.observableArrayList(User.UserType.values()));
        typeCombo.getSelectionModel().select(User.UserType.NORMAL);
    }

    @FXML
    void onCancel(ActionEvent event) {
        closeWindow();
    }

    @FXML
    void onSave(ActionEvent event) {
        if (emailField.getText().isEmpty() || passwordField.getText().isEmpty()) {
            showAlert("Validation Error", "Please fill in all fields.");
            return;
        }

        try {
            AppController.getInstance().createUser(
                    emailField.getText(),
                    passwordField.getText(),
                    typeCombo.getValue());
            showAlert("Success", "User created successfully.");
            closeWindow();
        } catch (IllegalArgumentException e) {
            showAlert("Error", e.getMessage());
        }
    }

    private void closeWindow() {
        Stage stage = (Stage) emailField.getScene().getWindow();
        // If this is opened as a modal (Dialog), close it.
        // If it was injected in the Dashboard, we can't 'close' it easily unless we
        // switched view.
        // But since this is administration, usually it's fine to just stay or clear.
        // However, in DashboardController I used `loadView` which replaces content.
        // So 'closing' doesn't mean much here if it's not a separate Stage.

        // Let's assume for now it stays there, or provides feedback.
        // If it is in a Stage (which `closeWindow` implies), it closes.
        // If it's part of the dashboard, `getWindow()` returns the Main Window. We
        // definitely DON'T want to close that.

        // Wait, where do I open this? In DashboardController I do:
        // `loadView("user-create-view.fxml")`.
        // So it is inside the dashboard.
        // So I should NOT close the window.
        // I should probably clear the fields or show a success message.

        if (stage != null && stage.getTitle().equals("BugBoard")) {
            // It's the main window. Do nothing or clear fields.
            emailField.clear();
            passwordField.clear();
        } else {
            stage.close();
        }
    }

    private void showAlert(String header, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        if (header.equals("Error"))
            alert.setAlertType(Alert.AlertType.ERROR);
        alert.setTitle(header);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
