package com.unina.bugboardapp.gui;

import com.unina.bugboardapp.StartApplication;
import com.unina.bugboardapp.controller.AppController;
import com.unina.bugboardapp.dialog.ErrorDialog;
import com.unina.bugboardapp.dialog.InfoDialog;
import com.unina.bugboardapp.dialog.WarningDialog;
import com.unina.bugboardapp.model.enums.UserType;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import java.util.logging.Logger;

import java.io.IOException;
import java.util.regex.Pattern;

/**
 * Controller JavaFX per la creazione di un nuovo utente.
 * <p>
 * La vista consente di inserire email, password e tipo utente, validare i dati e,
 * in caso di esito positivo, invocare la creazione tramite {@link AppController}.
 * Gli esiti vengono comunicati tramite dialog (warning/info/error).
 * </p>
 *
 * <h2>Validazione</h2>
 * <ul>
 *   <li>tutti i campi devono essere compilati;</li>
 *   <li>password con lunghezza minima {@link #MIN_PASSWORD_LENGTH};</li>
 *   <li>formato email valido tramite {@link #EMAIL_PATTERN};</li>
 *   <li>email non già registrata (verifica via {@link AppController#existsUser(String)}).</li>
 * </ul>
 *
 * <h2>Navigazione/chiusura</h2>
 * <p>
 * Alla chiusura, se è presente un {@link StackPane} con id {@code contentArea} nella scena corrente,
 * viene rimpiazzato il contenuto caricando {@code issue-list-view.fxml}; altrimenti viene chiusa
 * la finestra corrente.
 * </p>
 */
public class UserCreateGUI {
    private static final Logger logger = Logger.getLogger(UserCreateGUI.class.getName());
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[a-zA-Z0-9_+&*-]++(?:\\.[a-zA-Z0-9_+&*-]++)*+@(?:[a-zA-Z0-9-]++\\.)++[a-zA-Z]{2,7}$");

    private static final int MIN_PASSWORD_LENGTH = 3;
    private static final String ERROR_MESSAGE = "Validation Error";
    private static final String GENERIC_ERROR = "Error";

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private ComboBox<UserType> typeCombo;

    /**
     * Inizializza la view popolando la combo con i valori dell'enum e selezionando {@link UserType#USER}.
     */
    @FXML
    public void initialize() {
        typeCombo.setItems(FXCollections.observableArrayList(UserType.values()));
        typeCombo.getSelectionModel().select(UserType.USER);
    }

    /**
     * Handler del pulsante annulla: chiude la finestra o torna alla vista precedente.
     *
     * @param event evento JavaFX associato; può essere {@code null}
     */
    @FXML
    void onCancel(ActionEvent event) {
        closeWindow();
    }

    /**
     * Handler del pulsante salva.
     * <p>
     * Valida i dati; in caso di errori mostra un {@link WarningDialog}.
     * Se la validazione passa, crea l'utente e mostra un {@link InfoDialog}, quindi chiude/torna indietro.
     * Eventuali {@link IllegalArgumentException} vengono mostrate con {@link ErrorDialog}.
     * </p>
     *
     * @param event evento JavaFX associato; può essere {@code null}
     */
    @FXML
    void onSave(ActionEvent event) {
        try {
            ValidationResult validation = validateInput();
            if (!validation.valid()) {
                new WarningDialog(ERROR_MESSAGE, validation.errorMessage()).show();
                return;
            }

            createUserAndShowSuccess();
            closeWindow();
        } catch (IllegalArgumentException e) {
            new ErrorDialog(GENERIC_ERROR, e.getMessage()).show();
        }
    }

    /**
     * Esegue la validazione dei campi inseriti.
     *
     * @return un {@link ValidationResult} con esito e messaggio di errore (se presente)
     */
    private ValidationResult validateInput() {
        String email = emailField.getText();
        String password = passwordField.getText();

        if (email.isBlank() || password.isBlank()) {
            return ValidationResult.error("Please fill in all fields.");
        }

        if (password.length() < MIN_PASSWORD_LENGTH) {
            return ValidationResult.error("Password too short!");
        }

        if (!EMAIL_PATTERN.matcher(email).matches()) {
            return ValidationResult.error("Invalid email format!");
        }
        if(AppController.getInstance().existsUser(email))
            return ValidationResult.error("User already exists!");


        return ValidationResult.success();
    }

    /**
     * Crea l'utente delegando ad {@link AppController} e mostra un dialog di successo.
     * <p>
     * Usa i valori correnti dei campi email/password e del combo {@link #typeCombo}.
     * </p>
     */
    private void createUserAndShowSuccess() {
        AppController.getInstance().createUser(
                emailField.getText(),
                passwordField.getText(),
                typeCombo.getValue());
        new InfoDialog("Success", "User created successfully.").show();
    }

    /**
     * Chiude la view corrente.
     * <p>
     * Se esiste un nodo con id {@code contentArea} nella scena, carica {@code issue-list-view.fxml}
     * e lo imposta come contenuto; altrimenti chiude lo {@link Stage} corrente.
     * </p>
     * <p>
     * In caso di {@link IOException} mostra un {@link ErrorDialog}.
     * </p>
     */
    private void closeWindow() {
        try {
            StackPane contentArea = (StackPane) emailField.getScene().lookup("#contentArea");

            if (contentArea != null) {
                FXMLLoader loader = new FXMLLoader(StartApplication.class.getResource("issue-list-view.fxml"));
                Node view = loader.load();
                contentArea.getChildren().setAll(view);
            } else {
                Stage stage = (Stage) emailField.getScene().getWindow();
                stage.close();
            }
        } catch (IOException e) {
            logger.warning("Failed to return to the dashboard.");
            new ErrorDialog(GENERIC_ERROR, "Could not return to the dashboard.").show();
        }
    }

    /**
     * Risultato della validazione dei campi di input.
     *
     * @param valid indica se la validazione è passata
     * @param errorMessage messaggio di errore associato (può essere {@code null} se {@code valid} è {@code true})
     */
    private record ValidationResult(boolean valid, String errorMessage) {

        static ValidationResult success() {
            return new ValidationResult(true, null);
        }

        static ValidationResult error(String message) {
            return new ValidationResult(false, message);
        }
    }
}