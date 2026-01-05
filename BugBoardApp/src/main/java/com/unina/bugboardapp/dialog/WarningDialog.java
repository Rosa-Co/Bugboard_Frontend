package com.unina.bugboardapp.dialog;

import javafx.scene.control.Alert;

/**
 * Dialog di avviso basato su {@link BaseAlertDialog}.
 * <p>
 * Configura un {@link javafx.scene.control.Alert} come {@link javafx.scene.control.Alert.AlertType#WARNING}
 * con titolo predefinito "Attenzione" e con header e contenuto forniti dal chiamante.
 * </p>
 *
 * <p>
 * La visualizzazione avviene in modalità bloccante tramite {@code showAndWait()}.
 * </p>
 */
public class WarningDialog extends BaseAlertDialog {

    /**
     * Crea e configura un dialog di avviso.
     * <p>
     * Imposta:
     * <ul>
     *   <li>Tipo alert: {@link javafx.scene.control.Alert.AlertType#WARNING}</li>
     *   <li>Titolo: "Attenzione"</li>
     *   <li>Header: valore di {@code header}</li>
     *   <li>Contenuto: valore di {@code content}</li>
     * </ul>
     * </p>
     *
     * @param header  testo dell'intestazione (header) del dialog (può essere {@code null})
     * @param content testo del contenuto del dialog (può essere {@code null})
     */
    public WarningDialog(String header, String content) {
        super();
        getAlert().setAlertType(Alert.AlertType.WARNING);
        setTitleAndHeader("Attenzione", header);
        setContent(content);
    }

    /**
     * Mostra il dialog in modo modale/bloccante e attende la chiusura da parte dell'utente.
     *
     * @return questa istanza, per consentire uno stile fluente
     */
    @Override
    public WarningDialog show() {
        getAlert().showAndWait();
        return this;
    }
}