package com.unina.bugboardapp.controller;

import com.unina.bugboardapp.exception.CommentException;
import com.unina.bugboardapp.model.Comment;
import com.unina.bugboardapp.model.Issue;
import com.unina.bugboardapp.service.CommentService;
import javafx.application.Platform;

import java.util.List;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CommentController {
    private static final Logger logger = Logger.getLogger(CommentController.class.getName());
    private final CommentService commentService;
    private final AppState appState;

    public CommentController(AppState appState) {
        this.appState = appState;
        this.commentService = new CommentService();
    }

    public void addComment(Issue issue, String content, Consumer<Comment> onSuccess) {
        if (!appState.isLoggedIn()) {
            throw new IllegalStateException("User must be logged in to add comments");
        }
        if (issue == null) throw new IllegalArgumentException("Issue cannot be null");
        if (content == null || content.trim().isEmpty()) throw new IllegalArgumentException("Comment content cannot be empty");

        Comment newComment = new Comment(appState.getLoggedUser(), content.trim(), issue.getId());
        new Thread(() -> {
            try {
                Comment createdComment = commentService.createComment(newComment);
                Platform.runLater(() -> {
                    if (createdComment != null) {
                        issue.addComment(createdComment);
                        logger.info("Commento creato su server e UI");
                        if (onSuccess != null) onSuccess.accept(createdComment);
                    }
                });
            } catch (CommentException e) {
                logger.log(Level.SEVERE, "Errore durante la creazione del commento", e);
            } catch (Exception e1) {
                logger.log(Level.SEVERE, "Errore inaspettato durante la creazione del commento", e1);
            }
        }).start();
    }

    public void loadCommentsForIssue(Issue issue) {
        new Thread(() -> {
            try {
                List<Comment> comments = commentService.getCommentsByIssueId(issue.getId());
                Platform.runLater(() -> {
                    if (comments != null) {
                        issue.setComments(comments);
                    }
                });
            } catch (CommentException e) {
                logger.log(Level.SEVERE, "Errore durante il caricamento dei commenti", e);
            } catch (Exception e1) {
                logger.log(Level.SEVERE, "Errore inaspettato durante il caricamento dei commenti", e1);
            }
        }).start();
    }
}