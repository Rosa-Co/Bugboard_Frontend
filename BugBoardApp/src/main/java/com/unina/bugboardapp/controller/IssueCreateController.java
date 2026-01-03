package com.unina.bugboardapp.controller;

import com.unina.bugboardapp.model.IssueType;
import com.unina.bugboardapp.model.Priority;
import com.unina.bugboardapp.model.IssueState;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class IssueCreateController {

    @FXML
    private TextField titleField;

    @FXML
    private ComboBox<IssueType> typeCombo;

    @FXML
    private ComboBox<Priority> priorityCombo;

    @FXML
    private TextArea descriptionArea;

    @FXML
    private TextField imagePathField;

    @FXML
    public void initialize() {
        typeCombo.setItems(FXCollections.observableArrayList(IssueType.values()));
        priorityCombo.setItems(FXCollections.observableArrayList(Priority.values()));

        typeCombo.getSelectionModel().selectFirst();
        priorityCombo.getSelectionModel().selectFirst();
    }

    @FXML
    void onCancel(ActionEvent event) {
        closeWindow();
    }

    @FXML
    void onSave(ActionEvent event) {
        if (titleField.getText().isEmpty() || descriptionArea.getText().isEmpty()) {
            showAlert("Validation Error", "Please fill in title and description.");
            return;
        }

        AppController.getInstance().createIssue(
                titleField.getText(),
                descriptionArea.getText(),
                typeCombo.getValue(),
                priorityCombo.getValue(),
                imagePathField.getText(),
                IssueState.TODO);
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) titleField.getScene().getWindow();
        stage.close();
    }

    private void showAlert(String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
