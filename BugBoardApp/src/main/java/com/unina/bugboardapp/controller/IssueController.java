package com.unina.bugboardapp.controller;

import com.unina.bugboardapp.exception.IssueException;
import com.unina.bugboardapp.model.*;
import com.unina.bugboardapp.model.enums.IssueState;
import com.unina.bugboardapp.model.enums.IssueType;
import com.unina.bugboardapp.model.enums.Priority;
import com.unina.bugboardapp.service.IssueService;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class IssueController {
    private static final Logger logger = Logger.getLogger(IssueController.class.getName());
    private final IssueService issueService;
    private final AppState appState;

    public IssueController(AppState appState) {
        this.appState = appState;
        this.issueService = new IssueService();
    }

    public void refreshData() {
        new Thread(() -> {
            try {
                List<Issue> realIssues = issueService.fetchAllIssues();
                Platform.runLater(() -> {
                    appState.getIssues().clear();
                    if (realIssues != null) {
                        appState.getIssues().addAll(realIssues);
                    }
                    logger.info("Dati aggiornati dal backend!");
                });
            } catch (IssueException e) {
                logger.warning("Impossibile scaricare le issue: " + e.getMessage());
            } catch (Exception e1) {
                logger.warning("Errore inaspettato durante il download dei dati: " + e1.getMessage());
            }
        }).start();
    }

    public void createIssue(String title, String description, IssueType type,
                            Priority priority, String imagePath, IssueState state) {
        validateIssueInput(title, description, type, priority, state);

        Issue newIssue = new Issue(type, title, description, null, state, priority, appState.getLoggedUser());
        if (imagePath != null && !imagePath.trim().isEmpty()) {
            newIssue.setImagePath(imagePath.trim());
        }

        new Thread(() -> {
            try {
                Issue createdIssue = issueService.createIssue(newIssue);
                Platform.runLater(() -> {
                    if (createdIssue != null) {
                        appState.getIssues().add(createdIssue);
                        logger.info("Issue creata su server e UI");
                    }
                });
            } catch (IssueException e) {
                logger.log(Level.SEVERE, "Errore durante la creazione della Issue", e);
            } catch (Exception e1) {
                logger.log(Level.SEVERE, "Errore inaspettato durante la creazione della Issue", e1);
            }
        }).start();
    }

    private void validateIssueInput(String title, String description, IssueType type, Priority priority, IssueState state) {
        if (!appState.isLoggedIn()) {
            throw new IllegalStateException("User must be logged in to create issues");
        }
        if (title == null || title.trim().isEmpty()) throw new IllegalArgumentException("Issue title cannot be empty");
        if (description == null || description.trim().isEmpty()) throw new IllegalArgumentException("Issue description cannot be empty");
        if (type == null || priority == null || state == null) throw new IllegalArgumentException("Issue type, state and priority cannot be null");
    }

    public ObservableList<Issue> getIssuesFiltered(IssueType type) {
        if (type == null) return FXCollections.observableArrayList(appState.getIssues());
        return appState.getIssues().stream()
                .filter(i -> i.getType() == type)
                .collect(Collectors.toCollection(FXCollections::observableArrayList));
    }

    public ObservableList<Issue> getIssuesByPriority(Priority priority) {
        if (priority == null) return FXCollections.observableArrayList(appState.getIssues());
        return appState.getIssues().stream()
                .filter(i -> i.getPriority() == priority)
                .collect(Collectors.toCollection(FXCollections::observableArrayList));
    }

    public ObservableList<Issue> getIssuesByState(IssueState state) {
        if (state == null) return FXCollections.observableArrayList(appState.getIssues());
        return appState.getIssues().stream()
                .filter(i -> i.getState() == state)
                .collect(Collectors.toCollection(FXCollections::observableArrayList));
    }
}