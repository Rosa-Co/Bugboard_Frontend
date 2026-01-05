package com.unina.bugboardapp.gui;

import com.unina.bugboardapp.controller.AppController;
import com.unina.bugboardapp.exception.CommentException;
import com.unina.bugboardapp.model.Comment;
import com.unina.bugboardapp.model.Issue;

import com.unina.bugboardapp.service.CommentService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

import java.io.File;
import java.util.List;

public class IssueDetailGUI {

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
    private final CommentService commentService = new CommentService();
    private final com.unina.bugboardapp.service.IssueService issueService = new com.unina.bugboardapp.service.IssueService();

    public void setIssue(Issue issue) {
        this.issue = issue;
        updateUI();
    }

    private void updateUI() {
        if (issue == null) {
            return;
        }

        updateBasicInfo();
        updateImage();
        updateComments();
    }

    private void updateBasicInfo() {
        titleLabel.setText(issue.getTitle());
        typeLabel.setText(issue.getType().toString());
        stateLabel.setText(issue.getState().toString());
        priorityLabel.setText(issue.getPriority().toString() + " Priority");
        reporterLabel.setText(issue.getReporter().getUsername());
        descriptionLabel.setText(issue.getDescription());
    }

    private void updateImage() {
        if (issue.getImagePath() == null || issue.getImagePath().isEmpty()) {
            hideImage();
            return;
        }

        if (tryLoadLocalImage()) {
            imageContainer.setVisible(true);
            imageContainer.setManaged(true);
            return;
        }

        loadImageAsync();
    }

    private boolean tryLoadLocalImage() {
        File file = new File(issue.getImagePath());
        if (file.exists()) {
            imageView.setImage(new Image(file.toURI().toString()));
            return true;
        }
        return false;
    }

    private void loadImageAsync() {
        new Thread(() -> {
            try {
                java.io.InputStream is = issueService.downloadImage(issue.getImagePath());
                Image img = new Image(is);
                javafx.application.Platform.runLater(() -> {
                    imageView.setImage(img);
                    imageContainer.setVisible(true);
                    imageContainer.setManaged(true);
                });
            } catch (Exception e) {
                javafx.application.Platform.runLater(this::hideImage);
            }
        }).start();
    }

    private void updateComments() {
        commentsList.getChildren().clear();

        try {
            List<Comment> comments = commentService.getCommentsByIssueId(issue.getId());
            if (comments != null) {
                comments.forEach(this::addCommentToVBox);
            }
        } catch (CommentException e) {
            throw new IllegalStateException("Error retrieving comments for issue " + issue.getId(), e);
        }
    }

    private void hideImage() {
        imageContainer.setVisible(false);
        imageContainer.setManaged(false);
    }

    @FXML
    void onAddComment(ActionEvent event) {
        if (issue == null || commentArea.getText().trim().isEmpty())
            return;

        AppController.getInstance().addComment(issue, commentArea.getText(),
                createdComment -> {
                    commentArea.clear();
                    addCommentToVBox(createdComment);
                });

    }

    private void addCommentToVBox(Comment comment) {
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
}
