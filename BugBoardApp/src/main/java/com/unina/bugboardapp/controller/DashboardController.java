package com.unina.bugboardapp.controller;

import com.unina.bugboardapp.StartApplication;
import com.unina.bugboardapp.model.User;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;

public class DashboardController {

    @FXML
    private Label userInfoLabel;

    @FXML
    private Button btnIssues;

    @FXML
    private Button btnUsers;

    @FXML
    private StackPane contentArea;

    @FXML
    public void initialize() {
        User user = AppController.getInstance().getLoggedUser();
        if (user != null) {
            userInfoLabel.setText(user.getUsername() + " (" + user.getType() + ")");
            if (user.getType() != User.UserType.ADMIN) {
                btnUsers.setVisible(false);
                btnUsers.setManaged(false);
            }
        }
        // Load default view (Issues)
        Platform.runLater(() -> onIssuesClick(null));
    }

    @FXML
    void onIssuesClick(ActionEvent event) {
        // Load Issue List View
        loadView("issue-list-view.fxml");
    }

    @FXML
    void onUsersClick(ActionEvent event) {
        // Load User Create View
        loadView("user-create-view.fxml");
    }

    @FXML
    void onLogoutClick(ActionEvent event) {
        AppController.getInstance().logout();
        try {
            FXMLLoader loader = new FXMLLoader(StartApplication.class.getResource("login-view.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) userInfoLabel.getScene().getWindow();
            Scene loginScene = new Scene(root);
            stage.setScene(loginScene);
            stage.setTitle("BugBoard");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadView(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(StartApplication.class.getResource(fxmlFile));
            Node view = loader.load();
            contentArea.getChildren().setAll(view);
        } catch (IOException e) {
            System.err.println("Failed to load view: " + fxmlFile);
            e.printStackTrace();
            contentArea.getChildren().clear();
            Label error = new Label("View not implemented yet: " + fxmlFile);
            error.setStyle("-fx-font-weight: bold; -fx-text-fill: red; -fx-size: 16px;");
            contentArea.getChildren().add(error);
        }
    }
}
