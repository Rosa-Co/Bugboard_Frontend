package com.unina.bugboardapp.dialog;

import atlantafx.base.theme.Styles;
import javafx.scene.control.Alert;

public class ErrorDialog extends BaseAlertDialog {
    public ErrorDialog(String header, String content) {
        super();
        getAlert().setAlertType(Alert.AlertType.ERROR);
        setTitleAndHeader("Errore", header);
        setContent(content);

        getAlert().getDialogPane().getStyleClass().add(Styles.DANGER);
    }

    @Override
    public ErrorDialog show() {
        getAlert().showAndWait();
        return this;
    }
}
