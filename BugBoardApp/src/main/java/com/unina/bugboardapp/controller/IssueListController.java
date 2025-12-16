package com.unina.bugboardapp.controller;

import com.unina.bugboardapp.StartApplication;
import com.unina.bugboardapp.model.Issue;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class IssueListController {

    @FXML
    private TextField searchField;

    @FXML
    private ComboBox<Issue.IssueType> typeFilter;

    @FXML
    private ComboBox<Issue.IssueState> stateFilter;

    @FXML
    private TableView<Issue> issueTable;

    @FXML
    private TableColumn<Issue, Integer> colId;
    @FXML
    private TableColumn<Issue, String> colTitle;
    @FXML
    private TableColumn<Issue, Issue.IssueType> colType;
    @FXML
    private TableColumn<Issue, Issue.Priority> colPriority;
    @FXML
    private TableColumn<Issue, Issue.IssueState> colState;
    @FXML
    private TableColumn<Issue, String> colReporter;
    @FXML
    private TableColumn<Issue, String> colDate;

    private ObservableList<Issue> masterData;

    @FXML
    public void initialize() {
        // Setup Columns
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        colType.setCellValueFactory(new PropertyValueFactory<>("type"));
        colPriority.setCellValueFactory(new PropertyValueFactory<>("priority"));
        colState.setCellValueFactory(new PropertyValueFactory<>("state"));
        colReporter.setCellValueFactory(
                cellData -> new SimpleStringProperty(cellData.getValue().getReporter().getUsername()));
        colDate.setCellValueFactory(
                cellData -> new SimpleStringProperty(cellData.getValue().getCreatedAt().toString()));

        // Setup Filters
        typeFilter.setItems(FXCollections.observableArrayList(Issue.IssueType.values()));
        stateFilter.setItems(FXCollections.observableArrayList(Issue.IssueState.values()));

        // Load Data
        masterData = AppController.getInstance().getAllIssues();
        FilteredList<Issue> filteredData = new FilteredList<>(masterData, p -> true);

        // Filter Logic
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(issue -> isMatch(issue, newValue, typeFilter.getValue(), stateFilter.getValue()));
        });
        typeFilter.valueProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(issue -> isMatch(issue, searchField.getText(), newValue, stateFilter.getValue()));
        });
        stateFilter.valueProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(issue -> isMatch(issue, searchField.getText(), typeFilter.getValue(), newValue));
        });

        SortedList<Issue> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(issueTable.comparatorProperty());
        issueTable.setItems(sortedData);

        // Double click to open details
        issueTable.setRowFactory(tv -> {
            TableRow<Issue> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    Issue rowData = row.getItem();
                    openDetailView(rowData);
                }
            });
            return row;
        });
    }

    private boolean isMatch(Issue issue, String searchText, Issue.IssueType type, Issue.IssueState state) {
        boolean matchText = true;
        if (searchText != null && !searchText.isEmpty()) {
            String lowerCaseFilter = searchText.toLowerCase();
            matchText = issue.getTitle().toLowerCase().contains(lowerCaseFilter) ||
                    issue.getDescription().toLowerCase().contains(lowerCaseFilter);
        }

        boolean matchType = true;
        if (type != null) {
            matchType = issue.getType() == type;
        }

        boolean matchState = true;
        if (state != null) {
            matchState = issue.getState() == state;
        }

        return matchText && matchType && matchState;
    }

    @FXML
    void onNewIssue(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(StartApplication.class.getResource("issue-create-view.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("New Issue");
            stage.setScene(new Scene(root));
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void openDetailView(Issue issue) {
        try {
            FXMLLoader loader = new FXMLLoader(StartApplication.class.getResource("issue-detail-view.fxml"));
            Parent root = loader.load();

            IssueDetailController controller = loader.getController();
            controller.setIssue(issue);

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Issue #" + issue.getId());
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
