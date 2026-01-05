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

/**
 * Controller JavaFX della vista di dettaglio di una {@link Issue}.
 * <p>
 * Mostra le informazioni principali della issue, un'eventuale immagine associata (da file locale
 * o da URL remoto) e l'elenco dei commenti. Permette inoltre l'aggiunta di un nuovo commento
 * tramite {@link AppController}.
 * </p>
 *
 * <h2>Gestione immagine</h2>
 * <ul>
 *   <li>Se {@code imagePath} è vuoto/nullo: l'area immagine viene nascosta.</li>
 *   <li>Se {@code imagePath} punta a un file esistente: viene caricata l'immagine locale.</li>
 *   <li>Se {@code imagePath} inizia con {@code http}: viene tentato il caricamento remoto.</li>
 *   <li>In caso di errori: l'area immagine viene nascosta.</li>
 * </ul>
 *
 * <h2>Commenti</h2>
 * <ul>
 *   <li>I commenti vengono recuperati tramite {@link CommentService}.</li>
 *   <li>L'aggiunta commento delega a {@link AppController#addComment(Issue, String, java.util.function.Consumer)}.</li>
 * </ul>
 */
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

    /**
     * Issue attualmente visualizzata; può essere {@code null} finché non viene impostata.
     */
    private Issue issue;

    /**
     * Servizio usato per recuperare i commenti associati alla issue.
     */
    private final CommentService commentService = new CommentService();
    private final com.unina.bugboardapp.service.IssueService issueService = new com.unina.bugboardapp.service.IssueService();

    /**
     * Imposta la {@link Issue} da visualizzare e aggiorna la UI.
     *
     * @param issue issue da mostrare; può essere {@code null} (in tal caso l'aggiornamento UI non avviene)
     */
    public void setIssue(Issue issue) {
        this.issue = issue;
        updateUI();
    }

    /**
     * Aggiorna l'intera interfaccia sulla base della {@link #issue} corrente.
     * <p>
     * Se {@link #issue} è {@code null} il metodo non esegue alcuna operazione.
     * </p>
     */
    private void updateUI() {
        if (issue == null) {
            return;
        }

        updateBasicInfo();
        updateImage();
        updateComments();
    }

    /**
     * Aggiorna i campi testuali principali (titolo, tipo, stato, priorità, reporter, descrizione).
     * <p>
     * Presuppone che {@link #issue} non sia {@code null}.
     * </p>
     */
    private void updateBasicInfo() {
        titleLabel.setText(issue.getTitle());
        typeLabel.setText(issue.getType().toString());
        stateLabel.setText(issue.getState().toString());
        priorityLabel.setText(issue.getPriority().toString() + " Priority");
        reporterLabel.setText(issue.getReporter().getUsername());
        descriptionLabel.setText(issue.getDescription());
    }

    /**
     * Aggiorna la sezione immagine in base al percorso associato alla {@link #issue}.
     * <p>
     * Se il percorso è assente o non caricabile (locale/remoto), richiama {@link #hideImage()}.
     * Eventuali eccezioni durante il caricamento vengono intercettate e trattate nascondendo
     * l'area immagine.
     * </p>
     */
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

    /**
     * Tenta di caricare l'immagine come file locale.
     *
     * @return {@code true} se il file esiste ed è stato impostato su {@link #imageView},
     *         {@code false} altrimenti
     */
    private boolean tryLoadLocalImage() {
        File file = new File(issue.getImagePath());
        if (file.exists()) {
            imageView.setImage(new Image(file.toURI().toString()));
            return true;
        }
        return false;
    }

    /**
     * Tenta di caricare l'immagine da un URL remoto.
     * <p>
     * Il caricamento viene tentato solo se {@code imagePath} inizia con {@code http}.
     * </p>
     *
     * @return {@code true} se il percorso sembra un URL e l'immagine è stata impostata su {@link #imageView},
     *         {@code false} altrimenti
     */
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

    /**
     * Recupera e visualizza i commenti associati alla {@link #issue}.
     * <p>
     * Pulisce prima il contenitore {@link #commentsList}. Se il servizio restituisce una lista non nulla,
     * ogni commento viene renderizzato tramite {@link #addCommentToVBox(Comment)}.
     * </p>
     *
     * @throws IllegalStateException se si verifica un {@link CommentException} durante il recupero dei commenti
     */
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

    /**
     * Nasconde l'area immagine rimuovendola anche dal layout.
     */
    private void hideImage() {
        imageContainer.setVisible(false);
        imageContainer.setManaged(false);
    }

    /**
     * Handler del click su "Add Comment".
     * <p>
     * Se {@link #issue} non è impostata o il testo del commento è vuoto/blank, non fa nulla.
     * In caso contrario delega la creazione del commento ad {@link AppController}; al successo:
     * <ul>
     *   <li>pulisce {@link #commentArea}</li>
     *   <li>aggiunge il commento creato alla lista tramite {@link #addCommentToVBox(Comment)}</li>
     * </ul>
     * </p>
     *
     * @param event evento JavaFX associato all'azione; può essere {@code null}
     */
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

    /**
     * Crea e aggiunge un "cell" grafico per un {@link Comment} all'interno di {@link #commentsList}.
     * <p>
     * La cella include:
     * <ul>
     *   <li>intestazione con autore e tempo relativo</li>
     *   <li>contenuto del commento con wrapping</li>
     * </ul>
     * </p>
     *
     * @param comment commento da visualizzare; non dovrebbe essere {@code null}
     */
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