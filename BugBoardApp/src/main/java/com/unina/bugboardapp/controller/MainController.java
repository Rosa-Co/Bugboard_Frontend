package com.unina.bugboardapp.controller;

import com.unina.bugboardapp.utils.AsyncHelper;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.VBox;

public class MainController {

    @FXML
    private VBox root;

    @FXML
    private Label messageLabel;

    @FXML
    private Button actionButton;

    @FXML
    private ProgressIndicator spinner;

    private AsyncHelper asyncHelper;

    @FXML
    public void initialize() {
        asyncHelper = new AsyncHelper(spinner);
        spinner.setVisible(false);
    }

    @FXML
    private void onAction() {
        messageLabel.setText("Loading...");
        actionButton.setDisable(true);

        asyncHelper.run(() -> {
            // Simulate async task
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            return "Operation Complete!";
        }, result -> {
            // On success
            messageLabel.setText(result);
            actionButton.setDisable(false);
        });
    }
}
