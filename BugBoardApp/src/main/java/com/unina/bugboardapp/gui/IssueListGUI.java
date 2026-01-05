package com.unina.bugboardapp.gui;

import com.unina.bugboardapp.StartApplication;
import com.unina.bugboardapp.controller.AppController;
import com.unina.bugboardapp.model.Issue;
import com.unina.bugboardapp.model.IssueType;
import com.unina.bugboardapp.model.Priority;
import com.unina.bugboardapp.model.IssueState;
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
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.util.logging.Logger;

import java.io.IOException;

public class IssueListGUI {

    @FXML
    private TextField searchField;

    @FXML
    private ComboBox<IssueType> typeFilter;

    @FXML
    private ComboBox<IssueState> stateFilter;

    @FXML
    private TableView<Issue> issueTable;

    @FXML
    private TableColumn<Issue, Integer> colId;
    @FXML
    private TableColumn<Issue, String> colTitle;
    @FXML
    private TableColumn<Issue, IssueType> colType;
    @FXML
    private TableColumn<Issue, Priority> colPriority;
    @FXML
    private TableColumn<Issue, IssueState> colState;
    @FXML
    private TableColumn<Issue, String> colReporter;

    private final ObservableList<Issue> masterData = FXCollections.observableArrayList();
    private static final Logger logger = Logger.getLogger(IssueListGUI.class.getName());
    @FXML
    public void initialize() {
        setupColumns();
        setupFiltersAndTable();
        ObservableList<Issue> sourceList = AppController.getInstance().getAllIssues();
        javafx.beans.binding.Bindings.bindContent(masterData, sourceList);
    }

    private void setupColumns() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        colType.setCellValueFactory(new PropertyValueFactory<>("type"));
        colPriority.setCellValueFactory(new PropertyValueFactory<>("priority"));
        colState.setCellValueFactory(new PropertyValueFactory<>("state"));
        colReporter.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getReporter().getUsername()));
    }

    private void setupFiltersAndTable() {
        typeFilter.setItems(FXCollections.observableArrayList(IssueType.values()));
        stateFilter.setItems(FXCollections.observableArrayList(IssueState.values()));

        FilteredList<Issue> filteredData = new FilteredList<>(masterData, p -> true);

        searchField.textProperty().addListener((obs, oldVal, newVal) -> filteredData
                .setPredicate(issue -> isMatch(issue, newVal, typeFilter.getValue(), stateFilter.getValue())));

        typeFilter.valueProperty().addListener((obs, oldVal, newVal) -> filteredData
                .setPredicate(issue -> isMatch(issue, searchField.getText(), newVal, stateFilter.getValue())));

        stateFilter.valueProperty().addListener((obs, oldVal, newVal) -> filteredData
                .setPredicate(issue -> isMatch(issue, searchField.getText(), typeFilter.getValue(), newVal)));

        SortedList<Issue> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(issueTable.comparatorProperty());
        issueTable.setItems(sortedData);

        issueTable.setRowFactory(tv -> {
            TableRow<Issue> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    openDetailView(row.getItem());
                }
            });
            return row;
        });
    }

    private boolean isMatch(Issue issue, String searchText, IssueType type, IssueState state) {
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
            logger.severe("Failed to load issue create view");
        }
    }

    private void openDetailView(Issue issue) {
        try {
            FXMLLoader loader = new FXMLLoader(StartApplication.class.getResource("issue-detail-view.fxml"));
            Parent root = loader.load();

            IssueDetailGUI controller = loader.getController();
            controller.setIssue(issue);

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Issue #" + issue.getId());
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            logger.severe("Failed to load issue detail view");
        }
    }
}
