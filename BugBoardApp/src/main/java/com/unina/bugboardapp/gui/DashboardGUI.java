package com.unina.bugboardapp.gui;

import com.unina.bugboardapp.StartApplication;
import com.unina.bugboardapp.controller.AppController;
import com.unina.bugboardapp.model.User;
import com.unina.bugboardapp.model.UserType;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import java.util.logging.Level;
import java.util.logging.Logger;

import java.io.IOException;

/**
 * Controller JavaFX della dashboard principale dell'applicazione.
 * <p>
 * Gestisce:
 * <ul>
 *   <li>la visualizzazione delle informazioni dell'utente autenticato (username e tipo);</li>
 *   <li>l'abilitazione/visibilità delle funzionalità amministrative (es. gestione utenti);</li>
 *   <li>il caricamento delle viste FXML all'interno dell'area contenuti della dashboard;</li>
 *   <li>la procedura di logout e il ritorno alla schermata di login.</li>
 * </ul>
 * </p>
 *
 * <h2>Note</h2>
 * <ul>
 *   <li>I metodi annotati con {@link FXML} sono invocati dal loader FXML.</li>
 *   <li>Il caricamento iniziale della vista delle issue è programmato con
 *       {@link Platform#runLater(Runnable)} per garantire che la UI sia pronta.</li>
 * </ul>
 */
public class DashboardGUI {

    /**
     * Logger della classe, usato per tracciare errori durante caricamento viste e logout.
     */
    private static final Logger logger = Logger.getLogger(DashboardGUI.class.getName());

    /**
     * Label che mostra le informazioni dell'utente (username e tipo).
     */
    @FXML
    private Label userInfoLabel;

    /**
     * Pulsante di navigazione verso la vista delle issue.
     */
    @FXML
    private Button btnIssues;

    /**
     * Pulsante di navigazione verso la vista di gestione/creazione utenti (tipicamente solo admin).
     */
    @FXML
    private Button btnUsers;

    /**
     * Contenitore centrale in cui vengono caricate dinamicamente le viste FXML.
     */
    @FXML
    private StackPane contentArea;

    /**
     * Inizializza la dashboard dopo l'iniezione dei campi FXML.
     * <p>
     * Recupera l'utente loggato tramite {@link AppController#getInstance()} e:
     * <ul>
     *   <li>aggiorna {@code userInfoLabel} con username e tipo utente;</li>
     *   <li>nasconde e rimuove dal layout il pulsante {@code btnUsers} se l'utente non è
     *       {@link UserType#ADMIN};</li>
     *   <li>programma il caricamento della vista delle issue come contenuto iniziale.</li>
     * </ul>
     * </p>
     * <p>
     * Se nessun utente è loggato ({@code null}), non aggiorna la label e non modifica la visibilità
     * dei pulsanti.
     * </p>
     */
    @FXML
    public void initialize() {
        User user = AppController.getInstance().getLoggedUser();
        if (user != null) {
            userInfoLabel.setText(user.getUsername() + " (" + user.getType() + ")");
            if (user.getType() != UserType.ADMIN) {
                btnUsers.setVisible(false);
                btnUsers.setManaged(false);
            }
        }
        Platform.runLater(() -> onIssuesClick(null));
    }

    /**
     * Gestisce il click sul pulsante "Issues".
     * <p>
     * Carica la vista FXML {@code issue-list-view.fxml} all'interno di {@link #contentArea}.
     * </p>
     *
     * @param event evento JavaFX associato all'azione; può essere {@code null} quando invocato
     *              programmaticamente (es. da {@link #initialize()}).
     */
    @FXML
    void onIssuesClick(ActionEvent event) {
        loadView("issue-list-view.fxml");
    }

    /**
     * Gestisce il click sul pulsante "Users".
     * <p>
     * Carica la vista FXML {@code user-create-view.fxml} all'interno di {@link #contentArea}.
     * L'accesso è tipicamente limitato agli amministratori; l'abilitazione/visibilità è gestita
     * in {@link #initialize()}.
     * </p>
     *
     * @param event evento JavaFX associato all'azione; può essere {@code null}.
     */
    @FXML
    void onUsersClick(ActionEvent event) {
        loadView("user-create-view.fxml");
    }

    /**
     * Gestisce il click sul pulsante "Logout".
     * <p>
     * Esegue il logout tramite {@link AppController#logout()} e ripristina la schermata di login:
     * <ol>
     *   <li>carica {@code login-view.fxml};</li>
     *   <li>recupera lo {@link Stage} corrente dalla scena associata a {@link #userInfoLabel};</li>
     *   <li>imposta la nuova {@link Scene} e aggiorna il titolo della finestra.</li>
     * </ol>
     * </p>
     * <p>
     * In caso di errore durante il caricamento dell'FXML, l'eccezione viene registrata con livello
     * {@link Level#SEVERE}.
     * </p>
     *
     * @param event evento JavaFX associato all'azione; può essere {@code null}.
     */
    @FXML
    void onLogoutClick(ActionEvent event) {
        AppController.getInstance().logout();
        try {
            FXMLLoader loader = new FXMLLoader(StartApplication.class.getResource("login-view.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) userInfoLabel.getScene().getWindow();
            Scene loginScene = new Scene(root);
            stage.setScene(loginScene);
            stage.setTitle("BugBoard");
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Failed to logout", e);
        }
    }

    /**
     * Carica una vista FXML e la visualizza nell'area contenuti della dashboard.
     * <p>
     * In caso di successo, la vista caricata sostituisce tutti i nodi presenti in {@link #contentArea}.
     * In caso di errore I/O (risorsa non trovata, FXML non valido, ecc.):
     * <ul>
     *   <li>registra l'errore nel {@link #logger};</li>
     *   <li>pulisce {@link #contentArea};</li>
     *   <li>mostra una label di fallback con un messaggio informativo.</li>
     * </ul>
     * </p>
     *
     * @param fxmlFile nome del file FXML da caricare (risorsa risolta relativamente a
     *                 {@link StartApplication}); non dovrebbe essere {@code null}.
     */
    private void loadView(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(StartApplication.class.getResource(fxmlFile));
            Node view = loader.load();
            contentArea.getChildren().setAll(view);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Failed to load view", e);
            contentArea.getChildren().clear();
            Label error = new Label("View not implemented yet: " + fxmlFile);
            error.setStyle("-fx-font-weight: bold; -fx-text-fill: red; -fx-size: 16px;");
            contentArea.getChildren().add(error);
        }
    }
}