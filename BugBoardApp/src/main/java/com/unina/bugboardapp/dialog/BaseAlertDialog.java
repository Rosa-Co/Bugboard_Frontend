package com.unina.bugboardapp.dialog;

import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.layout.Region;
import atlantafx.base.theme.Styles;

/**
 * Classe base astratta per la creazione di dialog di tipo {@link Alert} con uno stile coerente.
 * <p>
 * Incapsula un'istanza di {@link Alert} configurata con:
 * <ul>
 *   <li>{@link Alert.AlertType#NONE} come tipo (per consentire personalizzazioni)</li>
 *   <li>il pulsante {@link ButtonType#OK} come azione predefinita</li>
 *   <li>applicazione di classi di stile (es. AtlantaFX) e dimensioni più leggibili</li>
 * </ul>
 * </p>
 *
 * <p>
 * Le sottoclassi possono specializzare la presentazione del dialog (es. tipo, icona, contenuto)
 * e devono implementare {@link #show()}.
 * </p>
 */
public abstract class BaseAlertDialog {

    /**
     * Istanza di {@link Alert} gestita da questa classe.
     */
    private Alert alert;

    /**
     * Costruisce un dialog base creando un {@link Alert} senza tipo predefinito,
     * aggiungendo il pulsante {@link ButtonType#OK} e applicando lo stile standard.
     */
    protected BaseAlertDialog() {
        alert = new Alert(Alert.AlertType.NONE);
        alert.getButtonTypes().add(ButtonType.OK);
        styleDialog();
    }

    /**
     * Applica la configurazione grafica standard al dialog:
     * <ul>
     *   <li>aggiunge classi di stile (es. {@link Styles#ROUNDED})</li>
     *   <li>imposta dimensioni preferite per migliorarne la leggibilità</li>
     *   <li>applica stile al pulsante OK (es. {@link Styles#ACCENT} e {@link Styles#ROUNDED})</li>
     * </ul>
     *
     * <p>
     * Metodo interno invocato dal costruttore.
     * </p>
     */
    private void styleDialog() {
        DialogPane dialogPane = alert.getDialogPane();

        // Applica le classi di stile AtlantaFX
        dialogPane.getStyleClass().add(Styles.ROUNDED);

        // Rendi il dialog più grande e leggibile
        dialogPane.setMinHeight(Region.USE_PREF_SIZE);
        dialogPane.setPrefWidth(450);

        // Stile per i bottoni
        dialogPane.lookupButton(ButtonType.OK).getStyleClass().addAll(
                Styles.ACCENT,
                Styles.ROUNDED
        );
    }

    /**
     * Imposta un'icona/graphic personalizzata per il dialog.
     * <p>
     * La {@code graphic} viene mostrata nell'header del dialog (area a sinistra del testo),
     * secondo il comportamento standard di JavaFX {@link Alert#setGraphic(Node)}.
     * </p>
     *
     * @param icon nodo grafico da utilizzare come icona; può essere {@code null} per rimuoverla
     */
    protected void setCustomIcon(Node icon) {
        alert.setGraphic(icon);
    }

    /**
     * Mostra il dialog.
     * <p>
     * Le sottoclassi definiscono come presentare la finestra (ad es. {@code show()} o {@code showAndWait()})
     * e possono effettuare ulteriori configurazioni prima della visualizzazione.
     * </p>
     *
     * @return l'istanza del dialog (tipicamente {@code this}) per consentire uno stile fluente
     */
    public abstract BaseAlertDialog show();

    /**
     * Imposta titolo e testo di intestazione del dialog.
     *
     * @param title      titolo della finestra
     * @param headerText testo dell'header (può essere {@code null} per nasconderlo)
     */
    public void setTitleAndHeader(String title, String headerText) {
        alert.setTitle(title);
        alert.setHeaderText(headerText);
    }

    /**
     * Imposta il contenuto testuale del dialog.
     *
     * @param content testo del contenuto (può essere {@code null})
     */
    public void setContent(String content) {
        alert.setContentText(content);
    }

    /**
     * Restituisce l'istanza di {@link Alert} incapsulata.
     * <p>
     * Metodo con visibilità package-private, utile per consentire a classi dello stesso package
     * ulteriori personalizzazioni/integrazioni senza esporre pubblicamente l'alert.
     * </p>
     *
     * @return l'istanza di {@link Alert} gestita dal dialog base
     */
    Alert getAlert() {
        return alert;
    }
}