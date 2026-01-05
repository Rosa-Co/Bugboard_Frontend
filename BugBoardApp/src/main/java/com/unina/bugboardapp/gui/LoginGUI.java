package com.unina.bugboardapp.gui;

import com.unina.bugboardapp.StartApplication;
import com.unina.bugboardapp.controller.AppController;
import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.util.logging.Level;
import java.util.logging.Logger;

import java.io.IOException;
import java.util.regex.Pattern;

/**
 * Controller JavaFX della schermata di login.
 * <p>
 * Gestisce:
 * <ul>
 *   <li>Validazione dei campi email/password (presenza, formato email, lunghezza minima password);</li>
 *   <li>Invocazione dell'autenticazione tramite {@link AppController#login(String, String)};</li>
 *   <li>Visualizzazione di messaggi di errore con animazioni di fade-in/fade-out;</li>
 *   <li>Navigazione alla dashboard in caso di login riuscito con transizione animata.</li>
 * </ul>
 * </p>
 *
 * <h2>Note UX</h2>
 * <ul>
 *   <li>Il pulsante di login viene disabilitato durante il tentativo di autenticazione.</li>
 *   <li>Premendo INVIO nel campo password viene eseguito il login.</li>
 *   <li>Modificando email o password, l'errore viene nascosto automaticamente.</li>
 * </ul>
 */
public class LoginGUI {
    private static final Logger logger = Logger.getLogger(LoginGUI.class.getName());
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[a-zA-Z0-9_+&*-]++(?:\\.[a-zA-Z0-9_+&*-]++)*+@(?:[a-zA-Z0-9-]++\\.)++[a-zA-Z]{2,7}$");
    private static final int MIN_PASSWORD_LENGTH = 3;
    private static final String DASHBOARD_VIEW = "dashboard-view.fxml";
    private static final String DASHBOARD_TITLE = "BugBoard - Dashboard";

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label errorLabel;

    @FXML
    private Button loginButton;


    /**
     * Inizializza la schermata di login dopo l'iniezione dei campi FXML.
     * <p>
     * Nasconde inizialmente {@link #errorLabel}, abilita il login con invio nel campo password e
     * aggiunge listener per nascondere l'errore quando l'utente modifica i campi.
     * </p>
     */
    @FXML
    public void initialize() {
        // Hide error label initially
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);

        // Add Enter key support
        passwordField.setOnAction(this::onLogin);

        emailField.textProperty().addListener((obs, oldVal, newVal) -> hideError());
        passwordField.textProperty().addListener((obs, oldVal, newVal) -> hideError());
    }
    /**
     * Handler dell'azione di login (click bottone o invio nel campo password).
     * <p>
     * Esegue:
     * <ol>
     *   <li>Lettura e normalizzazione input (trim email);</li>
     *   <li>Disabilitazione temporanea del bottone;</li>
     *   <li>Validazione input via {@link #validateInputs(String, String)};</li>
     *   <li>Invocazione {@link AppController#login(String, String)};</li>
     *   <li>Navigazione alla dashboard in caso di successo, altrimenti mostra errore e riabilita il bottone.</li>
     * </ol>
     * </p>
     *
     * @param event evento JavaFX associato all'azione; atteso non {@code null} quando invocato da UI
     */
    @FXML
    void onLogin(ActionEvent event) {
        String email = emailField.getText().trim();
        String password = passwordField.getText();

        loginButton.setDisable(true);

        if (!validateInputs(email, password)) {
            loginButton.setDisable(false);
            return;
        }

        boolean success = AppController.getInstance().login(email, password);

        if (success) {
            hideError();
            navigateToDashboard(event);
        } else {
            showError("Invalid email or password. Please try again.");
            loginButton.setDisable(false);
        }
    }
    /**
     * Valida i parametri di input lato UI.
     * <p>
     * Mostra un messaggio di errore contestuale tramite {@link #showError(String)} e posiziona il focus
     * sul campo pertinente in caso di errore.
     * </p>
     *
     * @param email email inserita (già trimmed dal chiamante)
     * @param password password inserita
     * @return {@code true} se gli input sono considerati validi, {@code false} altrimenti
     */
    private boolean validateInputs(String email, String password) {
        if (email.isEmpty() && password.isEmpty()) {
            showError("Please enter your email and password.");
            return false;
        }

        if (email.isEmpty()) {
            showError("Please enter your email address.");
            emailField.requestFocus();
            return false;
        }

        if (password.isEmpty()) {
            showError("Please enter your password.");
            passwordField.requestFocus();
            return false;
        }

        if (!isValidEmail(email)) {
            showError("Please enter a valid email address.");
            emailField.requestFocus();
            return false;
        }

        if (password.length() < MIN_PASSWORD_LENGTH) {
            showError("Password must be at least " + MIN_PASSWORD_LENGTH + " characters long.");
            passwordField.requestFocus();
            return false;
        }

        return true;
    }
    /**
     * Verifica se una stringa è un'email valida secondo {@link #EMAIL_PATTERN} e un limite di lunghezza.
     *
     * @param email email da verificare; può essere {@code null}
     * @return {@code true} se valida, {@code false} altrimenti
     */

    private boolean isValidEmail(String email) {
        return email != null
                && EMAIL_PATTERN.matcher(email).matches()
                && email.length() <= 254;
    }
    /**
     * Mostra un messaggio di errore nella UI con una breve animazione di fade-in.
     *
     * @param message testo da mostrare
     */
    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
        errorLabel.setManaged(true);

        FadeTransition fade = new FadeTransition(Duration.millis(300), errorLabel);
        fade.setFromValue(0.0);
        fade.setToValue(1.0);
        fade.play();
    }
    /**
     * Nasconde il messaggio di errore, se presente, con una breve animazione di fade-out.
     * <p>
     * Al termine dell'animazione rende anche non-managed la label per non occupare spazio nel layout.
     * </p>
     */
    private void hideError() {
        if (errorLabel.isVisible()) {
            FadeTransition fade = new FadeTransition(Duration.millis(200), errorLabel);
            fade.setFromValue(1.0);
            fade.setToValue(0.0);
            fade.setOnFinished(e -> {
                errorLabel.setVisible(false);
                errorLabel.setManaged(false);
            });
            fade.play();
        }
    }
    /**
     * Naviga dalla scena di login alla dashboard caricando l' FXML e applicando una transizione.
     * <p>
     * In caso di errore di caricamento, registra l'eccezione e mostra un messaggio nella UI,
     * riabilitando il pulsante di login.
     * </p>
     *
     * @param event evento JavaFX che ha generato la navigazione; usato per ottenere {@link Stage} corrente
     */
    private void navigateToDashboard(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(StartApplication.class.getResource(DASHBOARD_VIEW));
            Parent root = loader.load();

            Scene currentScene = ((Node) event.getSource()).getScene();
            Stage stage = (Stage) currentScene.getWindow();

            Scene newScene = new Scene(root);

            FadeTransition fadeOut = createFadeTransition(currentScene, stage, newScene);
            fadeOut.play();

        } catch (IOException e) {
            logger.log(Level.SEVERE,"Failed to load dashboard" , e);
            showError("Unable to load dashboard. Please try again.");
            loginButton.setDisable(false);
        }
    }
    /**
     * Crea una transizione di cambio scena con fade-out della scena corrente e fade-in della nuova scena.
     * <p>
     * Al termine del fade-out:
     * <ul>
     *   <li>Imposta la nuova scena sullo stage;</li>
     *   <li>Aggiorna il titolo e centra la finestra;</li>
     *   <li>Avvia il fade-in della nuova root.</li>
     * </ul>
     * </p>
     *
     * @param currentScene scena attuale
     * @param stage stage su cui impostare la nuova scena
     * @param newScene nuova scena da mostrare
     * @return transizione di fade-out configurata
     */
    private FadeTransition createFadeTransition(Scene currentScene, Stage stage, Scene newScene) {
        FadeTransition fadeOut = new FadeTransition(Duration.millis(300), currentScene.getRoot());
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        fadeOut.setOnFinished(e -> {
            stage.setScene(newScene);
            stage.setTitle(DASHBOARD_TITLE);
            stage.centerOnScreen();

            FadeTransition fadeIn = new FadeTransition(Duration.millis(300), newScene.getRoot());
            fadeIn.setFromValue(0.0);
            fadeIn.setToValue(1.0);
            fadeIn.play();
        });
        return fadeOut;
    }
}
