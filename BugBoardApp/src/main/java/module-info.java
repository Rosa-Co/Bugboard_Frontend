module com.unina.bugboardapp {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.net.http;
    requires MaterialFX;
    requires atlantafx.base;
    requires com.fasterxml.jackson.databind;

    opens com.unina.bugboardapp to javafx.fxml;
    opens com.unina.bugboardapp.controller to javafx.fxml;
    opens com.unina.bugboardapp.service to com.fasterxml.jackson.databind; //aggiunta dopo
    exports com.unina.bugboardapp;
    exports com.unina.bugboardapp.controller;
    exports com.unina.bugboardapp.model;
}