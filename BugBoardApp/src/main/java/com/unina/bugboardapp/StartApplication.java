package com.unina.bugboardapp;

import atlantafx.base.theme.PrimerLight;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.logging.Logger;

public class StartApplication extends Application {

    private static final String APP_TITLE = "BugBoard";
    private static final String APP_VERSION = "2.0";
    private static final String LOGIN_VIEW = "login-view.fxml";
    private static final int DEFAULT_WIDTH = 800;
    private static final int DEFAULT_HEIGHT = 600;
    private static final int MIN_WIDTH = 600;
    private static final int MIN_HEIGHT = 400;
    private static final Logger logger = Logger.getLogger(StartApplication.class.getName());

    @Override
    public void start(Stage primaryStage) {
        try {
            Application.setUserAgentStylesheet(new PrimerLight().getUserAgentStylesheet());

            Scene scene = createScene(LOGIN_VIEW, DEFAULT_WIDTH, DEFAULT_HEIGHT);

            configurePrimaryStage(primaryStage, scene);

            primaryStage.show();

        } catch (IOException e) {
            logger.severe("Failed to start application: " + e.getMessage());
            System.exit(1);
        }
    }

    private Scene createScene(String fxmlFile, int width, int height) throws IOException {
        URL fxmlUrl = getResource(fxmlFile);
        if (fxmlUrl == null) {
            throw new IOException("Cannot find FXML file: " + fxmlFile);
        }

        FXMLLoader loader = new FXMLLoader(fxmlUrl);
        return new Scene(loader.load(), width, height);
    }

    private void configurePrimaryStage(Stage stage, Scene scene) {
        stage.setTitle(APP_TITLE + " v" + APP_VERSION);
        stage.setScene(scene);
        stage.setMinWidth(MIN_WIDTH);
        stage.setMinHeight(MIN_HEIGHT);
        stage.centerOnScreen();
    }

    private URL getResource(String resourcePath) {
        return StartApplication.class.getResource(resourcePath);
    }

    public static void main(String[] args) {
        launch(args);
    }

    public static String getAppTitle() {
        return APP_TITLE;
    }

    public static String getAppVersion() {
        return APP_VERSION;
    }
}
