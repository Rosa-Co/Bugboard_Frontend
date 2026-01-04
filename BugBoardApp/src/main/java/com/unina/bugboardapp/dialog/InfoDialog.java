package com.unina.bugboardapp.dialog;

import atlantafx.base.theme.Styles;
import javafx.scene.control.Alert;

public class InfoDialog extends BaseAlertDialog{
    public InfoDialog(String header, String content) {
        super();
        getAlert().setAlertType(Alert.AlertType.INFORMATION);
        setTitleAndHeader("Informazione", header);
        setContent(content);

        getAlert().getDialogPane().getStyleClass().add(Styles.SUCCESS);
    }

    @Override
    public InfoDialog show() {
        getAlert().showAndWait();
        return this;
    }
}
