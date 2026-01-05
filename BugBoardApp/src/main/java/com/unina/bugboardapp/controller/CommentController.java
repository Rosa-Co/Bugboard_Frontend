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

/**
 * Controller responsabile della gestione dei commenti associati alle {@link Issue}.
 * <p>
 * Fornisce operazioni per:
 * <ul>
 *   <li>aggiungere un commento a una issue (con creazione lato backend e aggiornamento lato UI)</li>
 *   <li>caricare i commenti di una issue dal backend</li>
 * </ul>
 * </p>
 *
 * <h2>Threading e UI</h2>
 * Le chiamate al backend vengono eseguite in un thread dedicato (tramite {@link Thread}) per non
 * bloccare la UI. Gli aggiornamenti del modello collegati alla UI vengono eseguiti nel JavaFX
 * Application Thread tramite {@link Platform#runLater(Runnable)}.
 *
 * <h2>Stato applicativo</h2>
 * Utilizza {@link AppState} per:
 * <ul>
 *   <li>verificare che l'utente sia autenticato</li>
 *   <li>recuperare l'utente corrente come autore dei commenti</li>
 * </ul>
 */
public class CommentController {
    private static final Logger logger = Logger.getLogger(CommentController.class.getName());
    private final CommentService commentService;
    private final AppState appState;

    /**
     * Crea un {@code CommentController} associato allo stato applicativo fornito.
     *
     * @param appState stato applicativo da cui leggere l'utente loggato e lo stato di login
     */
    public CommentController(AppState appState) {
        this.appState = appState;
        this.commentService = new CommentService();
    }

    /**
     * Aggiunge un commento a una {@link Issue}.
     * <p>
     * Esegue le seguenti validazioni:
     * <ul>
     *   <li>l'utente deve essere loggato ({@link AppState#isLoggedIn()})</li>
     *   <li>{@code issue} non deve essere {@code null}</li>
     *   <li>{@code content} non deve essere {@code null} né vuoto/blank</li>
     * </ul>
     * </p>
     *
     * <p>
     * La creazione effettiva avviene in background tramite {@link CommentService#createComment(Comment)}.
     * In caso di successo, il commento creato viene aggiunto alla issue (es. {@code issue.addComment(...)})
     * nel thread JavaFX e, se presente, viene invocata la callback {@code onSuccess}.
     * </p>
     *
     * @param issue     issue a cui associare il commento (non {@code null})
     * @param content   testo del commento (non {@code null} e non blank)
     * @param onSuccess callback opzionale invocata (nel JavaFX Application Thread) con il commento creato
     * @throws IllegalStateException    se non c'è un utente loggato
     * @throws IllegalArgumentException se {@code issue} è {@code null} oppure {@code content} non è valido
     */
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

    /**
     * Carica dal backend i commenti associati a una {@link Issue} e li imposta sul modello.
     * <p>
     * L'operazione viene eseguita in background tramite {@link CommentService#getCommentsByIssueId(Integer)}.
     * Al termine, nel JavaFX Application Thread, se la lista ottenuta è non {@code null}, viene invocato
     * {@code issue.setComments(comments)} per aggiornare i commenti della issue.
     * </p>
     *
     * <p><strong>Nota:</strong> questo metodo assume che {@code issue} sia non {@code null} e che
     * {@code issue.getId()} sia disponibile; eventuali errori vengono registrati via logger.</p>
     *
     * @param issue issue per cui caricare i commenti
     */
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