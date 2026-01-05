package com.unina.bugboardapp.gui;

import com.unina.bugboardapp.utils.AsyncHelper;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.VBox;

/**
 * Controller JavaFX di una vista principale di esempio.
 * <p>
 * Mostra un messaggio, un pulsante di azione e uno spinner. Al click del pulsante
 * avvia un'operazione asincrona tramite {@link AsyncHelper}, disabilita temporaneamente
 * l'azione e aggiorna la UI al completamento.
 * </p>
 *
 * <h2>Flusso</h2>
 * <ol>
 *   <li>All'inizializzazione crea {@link #asyncHelper} e nasconde lo spinner.</li>
 *   <li>Alla pressione del pulsante:
 *     <ul>
 *       <li>mostra un messaggio di caricamento e disabilita il pulsante;</li>
 *       <li>esegue un task in background;</li>
 *       <li>al successo aggiorna il messaggio con il risultato e riabilita il pulsante.</li>
 *     </ul>
 *   </li>
 * </ol>
 */
public class MainGUI {

    /**
     * Nodo root della vista (contenitore principale).
     * <p>
     * Utile come riferimento per eventuali operazioni UI (es. lookup, binding, stile).
     * </p>
     */
    @FXML
    private VBox root;

    @FXML
    private Label messageLabel;

    @FXML
    private Button actionButton;

    @FXML
    private ProgressIndicator spinner;

    private AsyncHelper asyncHelper;

    /**
     * Inizializza il controller dopo l'iniezione FXML.
     * <p>
     * Istanzia l'helper asincrono e imposta lo spinner non visibile.
     * </p>
     */
    @FXML
    public void initialize() {
        asyncHelper = new AsyncHelper(spinner);
        spinner.setVisible(false);
    }
    /**
     * Handler dell'azione associata al pulsante.
     * <p>
     * Aggiorna immediatamente la UI per indicare lo stato di caricamento e avvia
     * un'operazione in background. Al completamento, aggiorna il messaggio e riabilita
     * il pulsante.
     * </p>
     */
    @FXML
    private void onAction() {
        messageLabel.setText("Loading...");
        actionButton.setDisable(true);

        asyncHelper.run(() -> {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            return "Operation Complete!";
        }, result -> {
            messageLabel.setText(result);
            actionButton.setDisable(false);
        });
    }
}
