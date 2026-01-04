package com.unina.bugboardapp.dialog;

import javafx.scene.control.Alert;

public class WarningDialog extends BaseAlertDialog {
    public WarningDialog(String header, String content) {
        super();
        getAlert().setAlertType(Alert.AlertType.WARNING);
        setTitleAndHeader("Attenzione", header);
        setContent(content);
    }

    @Override
    public WarningDialog show() {
        getAlert().showAndWait();
        return this;
    }
}
