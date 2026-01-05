package com.unina.bugboardapp.dialog;

import atlantafx.base.theme.Styles;
import javafx.scene.control.Alert;

/**
 * Dialog di errore basato su {@link BaseAlertDialog}.
 * <p>
 * Configura un {@link Alert} con:
 * <ul>
 *   <li>Tipo {@link Alert.AlertType#ERROR}</li>
 *   <li>Titolo predefinito "Errore"</li>
 *   <li>Header e contenuto forniti dal chiamante</li>
 *   <li>Stile grafico "danger" (es. {@link Styles#DANGER}) per evidenziare l'errore</li>
 * </ul>
 * </p>
 *
 * <p>
 * La visualizzazione avviene in modalità bloccante tramite {@code showAndWait()}.
 * </p>
 */
public class ErrorDialog extends BaseAlertDialog {

    /**
     * Crea e configura un dialog di errore.
     *
     * @param header  testo dell'intestazione (header) del dialog; può essere {@code null}
     * @param content testo del contenuto del dialog; può essere {@code null}
     */
    public ErrorDialog(String header, String content) {
        super();
        getAlert().setAlertType(Alert.AlertType.ERROR);
        setTitleAndHeader("Errore", header);
        setContent(content);

        getAlert().getDialogPane().getStyleClass().add(Styles.DANGER);
    }

    /**
     * Mostra il dialog in modo modale/bloccante e attende la chiusura da parte dell'utente.
     *
     * @return questa istanza, per consentire uno stile fluente
     */
    @Override
    public ErrorDialog show() {
        getAlert().showAndWait();
        return this;
    }
}