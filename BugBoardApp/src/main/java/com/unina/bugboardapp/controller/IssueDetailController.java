package com.unina.bugboardapp.controller;

import com.unina.bugboardapp.model.Comment;
import com.unina.bugboardapp.model.Issue;

import com.unina.bugboardapp.service.BackendService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

import java.io.File;
import java.util.List;

public class IssueDetailController {

    @FXML
    private Label typeLabel;
    @FXML
    private Label stateLabel;
    @FXML
    private Label priorityLabel;
    @FXML
    private Label titleLabel;
    @FXML
    private Label reporterLabel;
    @FXML
    private Label dateLabel;
    @FXML
    private Label descriptionLabel;
    @FXML
    private VBox imageContainer;
    @FXML
    private ImageView imageView;
    @FXML
    private VBox commentsList;
    @FXML
    private TextArea commentArea;

    private Issue issue;
    private final BackendService backendService=new BackendService();

    public void setIssue(Issue issue) {
        this.issue = issue;
        updateUI();
    }

    private void updateUI() {
        if (issue == null)
            return;

        titleLabel.setText(issue.getTitle());
        typeLabel.setText(issue.getType().toString());
        stateLabel.setText(issue.getState().toString());
        priorityLabel.setText(issue.getPriority().toString() + " Priority");
        reporterLabel.setText(issue.getReporter().getUsername());
        dateLabel.setText(issue.getCreatedAt().toLocalDate().toString());
        descriptionLabel.setText(issue.getDescription());

        if (issue.getImagePath() != null && !issue.getImagePath().isEmpty()) {
            try {
                File file = new File(issue.getImagePath());
                if (file.exists()) {
                    imageView.setImage(new Image(file.toURI().toString()));
                } else {
                    if (issue.getImagePath().startsWith("http")) {
                        imageView.setImage(new Image(issue.getImagePath()));
                    } else {
                        imageContainer.setVisible(false);
                        imageContainer.setManaged(false);
                    }
                }
            } catch (Exception e) {
                imageContainer.setVisible(false);
                imageContainer.setManaged(false);
            }
        } else {
            imageContainer.setVisible(false);
            imageContainer.setManaged(false);
        }

        commentsList.getChildren().clear();
        List<Comment> comments=null;
        try{
            comments = backendService.getCommentsByIssueId(issue.getId());
        }catch(Exception e){
            e.printStackTrace();
        }
        if (comments != null) {
            for (Comment comment : comments) {
                VBox cell = new VBox(4);
                cell.setStyle(
                        "-fx-padding: 10; -fx-background-color: -color-bg-default; -fx-background-radius: 6; -fx-border-color: -color-border-subtle; -fx-border-radius: 6;");

                // Author and time
                Label header = new Label(comment.getAuthor().getUsername() + " · " + comment.getRelativeTime());
                header.setStyle("-fx-font-weight: bold; -fx-font-size: 12; -fx-text-fill: -color-fg-muted;");

                // Comment content
                Label content = new Label(comment.getContent());
                content.setWrapText(true);
                content.setStyle("-fx-font-size: 14; -fx-text-fill: -color-fg-default;");

                cell.getChildren().addAll(header, content);
                commentsList.getChildren().add(cell);
            }
        }
        /*commentsList.getChildren().clear();

        if (issue.getComments() != null) {
            for (Comment comment : issue.getComments()) {
                VBox cell = new VBox(4);
                cell.setStyle(
                        "-fx-padding: 10; -fx-background-color: -color-bg-default; -fx-background-radius: 6; -fx-border-color: -color-border-subtle; -fx-border-radius: 6;");
                Label header = new Label(comment.getAuthor().getUsername() + " · " + comment.getRelativeTime());

                header.setStyle("-fx-font-weight: bold; -fx-font-size: 12; -fx-text-fill: -color-fg-muted;");

                Label content = new Label(comment.getContent());
                content.setWrapText(true);
                content.setStyle("-fx-font-size: 14; -fx-text-fill: -color-fg-default;");

                cell.getChildren().addAll(header, content);
                commentsList.getChildren().add(cell);
            }
        }*/
    }

    @FXML
    void onAddComment(ActionEvent event) {
        if (issue == null || commentArea.getText().trim().isEmpty())
            return;

        AppController.getInstance().addComment(issue, commentArea.getText(),
                (createdComment)->{ commentArea.clear(); updateUI();});
        //commentArea.clear();
        //updateUI();
    }
}
