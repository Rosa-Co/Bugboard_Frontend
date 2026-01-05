package com.unina.bugboardapp.dialog;

import atlantafx.base.theme.Styles;
import javafx.scene.control.Alert;

/**
 * Dialog informativo basato su {@link BaseAlertDialog}.
 * <p>
 * Configura un {@link javafx.scene.control.Alert} come {@link javafx.scene.control.Alert.AlertType#INFORMATION}
 * con titolo predefinito "Informazione", header e contenuto forniti dal chiamante, e applica
 * una classe di stile di tipo "success" (es. {@link atlantafx.base.theme.Styles#SUCCESS})
 * al {@link javafx.scene.control.DialogPane}.
 * </p>
 *
 * <p>
 * La visualizzazione avviene in modalità bloccante tramite {@code showAndWait()}.
 * </p>
 */
public class InfoDialog extends BaseAlertDialog{

    /**
     * Crea e configura un dialog informativo.
     * <p>
     * Imposta:
     * <ul>
     *   <li>tipo alert: {@link javafx.scene.control.Alert.AlertType#INFORMATION}</li>
     *   <li>titolo: "Informazione"</li>
     *   <li>header: valore di {@code header}</li>
     *   <li>contenuto: valore di {@code content}</li>
     *   <li>stile: aggiunge {@link atlantafx.base.theme.Styles#SUCCESS} al dialog pane</li>
     * </ul>
     * </p>
     *
     * @param header  testo dell'intestazione del dialog (può essere {@code null})
     * @param content testo del contenuto del dialog (può essere {@code null})
     */
    public InfoDialog(String header, String content) {
        super();
        getAlert().setAlertType(Alert.AlertType.INFORMATION);
        setTitleAndHeader("Informazione", header);
        setContent(content);

        getAlert().getDialogPane().getStyleClass().add(Styles.SUCCESS);
    }

    /**
     * Mostra il dialog in modo modale/bloccante e attende la chiusura da parte dell'utente.
     *
     * @return questa istanza, per consentire uno stile fluente
     */
    @Override
    public InfoDialog show() {
        getAlert().showAndWait();
        return this;
    }
}