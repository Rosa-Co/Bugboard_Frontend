package com.unina.bugboardapp.gui;

import com.unina.bugboardapp.controller.AppController;
import com.unina.bugboardapp.model.IssueType;
import com.unina.bugboardapp.model.Priority;
import com.unina.bugboardapp.model.IssueState;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * Controller JavaFX della finestra di creazione di una nuova issue.
 * <p>
 * Gestisce l'inizializzazione dei controlli (combo di tipo e priorità), la validazione minima
 * dei campi richiesti e l'invio della richiesta di creazione al livello applicativo tramite
 * {@link AppController}.
 * </p>
 *
 * <h2>Comportamento</h2>
 * <ul>
 *   <li>All'avvio popola le combo con i valori delle enum e seleziona il primo elemento.</li>
 *   <li>Su "Save" valida che titolo e descrizione non siano vuoti; in caso contrario mostra un alert.</li>
 *   <li>Se la validazione passa, crea una issue in stato "to do" </li>
 *   <li>Su "Cancel" chiude semplicemente la finestra.</li>
 * </ul>
 */
public class IssueCreateGUI {

    /**
     * Campo di input per il titolo della issue.
     */
    @FXML
    private TextField titleField;

    /**
     * Selettore del tipo di issue.
     */
    @FXML
    private ComboBox<IssueType> typeCombo;

    /**
     * Selettore della priorità della issue.
     */
    @FXML
    private ComboBox<Priority> priorityCombo;

    /**
     * Area di testo per la descrizione della issue.
     */
    @FXML
    private TextArea descriptionArea;

    /**
     * Campo opzionale per il percorso dell'immagine associata alla issue.
     */
    @FXML
    private TextField imagePathField;

    /**
     * Inizializza i controlli della UI dopo l'iniezione FXML.
     * <p>
     * Popola {@link #typeCombo} e {@link #priorityCombo} con tutti i valori disponibili delle enum
     * e imposta una selezione predefinita (primo elemento).
     * </p>
     */
    @FXML
    public void initialize() {
        typeCombo.setItems(FXCollections.observableArrayList(IssueType.values()));
        priorityCombo.setItems(FXCollections.observableArrayList(Priority.values()));

        typeCombo.getSelectionModel().selectFirst();
        priorityCombo.getSelectionModel().selectFirst();
    }

    /**
     * Handler del click su "Cancel".
     * <p>
     * Chiude la finestra corrente senza salvare.
     * </p>
     *
     * @param event evento JavaFX associato all'azione; può essere {@code null}
     */
    @FXML
    void onCancel(ActionEvent event) {
        closeWindow();
    }

    /**
     * Handler del click su "Save".
     * <p>
     * Esegue una validazione minimale dei campi obbligatori (titolo e descrizione). Se non valida,
     * mostra un messaggio di errore. Altrimenti delega la creazione della issue a
     * {@link AppController#createIssue(String, String, IssueType, Priority, String, IssueState)}
     * </p>
     *
     * @param event evento JavaFX associato all'azione; può essere {@code null}
     */
    @FXML
    void onBrowseImage(ActionEvent event) {
        javafx.stage.FileChooser fileChooser = new javafx.stage.FileChooser();
        fileChooser.setTitle("Select Image");
        fileChooser.getExtensionFilters().addAll(
                new javafx.stage.FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif"));
        java.io.File selectedFile = fileChooser.showOpenDialog(imagePathField.getScene().getWindow());
        if (selectedFile != null) {
            imagePathField.setText(selectedFile.getAbsolutePath());
        }
    }

    @FXML
    void onSave(ActionEvent event) {
        if (titleField.getText().isEmpty() || descriptionArea.getText().isEmpty()) {
            showAlert("Validation Error", "Please fill in title and description.");
            return;
        }

        AppController.getInstance().createIssue(
                titleField.getText(),
                descriptionArea.getText(),
                typeCombo.getValue(),
                priorityCombo.getValue(),
                imagePathField.getText(),
                IssueState.TODO);
        closeWindow();
    }

    /**
     * Chiude lo {@link Stage} che contiene questo form.
     * <p>
     * Recupera la finestra corrente tramite la scena associata a {@link #titleField}.
     * </p>
     */
    private void closeWindow() {
        Stage stage = (Stage) titleField.getScene().getWindow();
        stage.close();
    }

    /**
     * Mostra un dialog di errore modale con un messaggio personalizzato.
     *
     * @param header testo dell'intestazione del dialog (header); può essere {@code null}
     * @param content testo del contenuto del dialog; può essere {@code null}
     */
    private void showAlert(String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}