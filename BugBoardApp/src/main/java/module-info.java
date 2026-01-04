module com.unina.bugboardapp {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.net.http;
    requires MaterialFX;
    requires atlantafx.base;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.datatype.jsr310;
    requires java.logging;

    opens com.unina.bugboardapp to javafx.fxml;
    opens com.unina.bugboardapp.controller to javafx.fxml;
    opens com.unina.bugboardapp.service to com.fasterxml.jackson.databind;
    opens com.unina.bugboardapp.model to com.fasterxml.jackson.databind;
    opens com.unina.bugboardapp.dto to com.fasterxml.jackson.databind;

    exports com.unina.bugboardapp;
    exports com.unina.bugboardapp.controller;
    exports com.unina.bugboardapp.model;
    exports com.unina.bugboardapp.service;
    exports com.unina.bugboardapp.dto;
    exports com.unina.bugboardapp.exception;
    exports com.unina.bugboardapp.utils;
}