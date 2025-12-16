package com.unina.bugboardapp;

import atlantafx.base.theme.PrimerLight;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

/**
 * BugBoard - Main Application Class
 * A modern bug tracking application built with JavaFX
 * 
 * @author BugBoard Team
 * @version 2.0
 */
public class StartApplication extends Application {

    // Application Constants
    private static final String APP_TITLE = "BugBoard";
    private static final String APP_VERSION = "2.0";
    private static final String LOGIN_VIEW = "login-view.fxml";
    private static final int DEFAULT_WIDTH = 800;
    private static final int DEFAULT_HEIGHT = 600;
    private static final int MIN_WIDTH = 600;
    private static final int MIN_HEIGHT = 400;

    @Override
    public void start(Stage primaryStage) {
        try {
            // Set AtlantaFX theme
            Application.setUserAgentStylesheet(new PrimerLight().getUserAgentStylesheet());

            // Load login view
            Scene scene = createScene(LOGIN_VIEW, DEFAULT_WIDTH, DEFAULT_HEIGHT);

            // Configure primary stage
            configurePrimaryStage(primaryStage, scene);

            // Show the application
            primaryStage.show();

        } catch (IOException e) {
            System.err.println("Failed to start application: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Creates a scene with the specified FXML view and dimensions
     * 
     * @param fxmlFile The FXML file name
     * @param width    Scene width
     * @param height   Scene height
     * @return Configured Scene object
     * @throws IOException If the FXML file cannot be loaded
     */
    private Scene createScene(String fxmlFile, int width, int height) throws IOException {
        URL fxmlUrl = getResource(fxmlFile);
        if (fxmlUrl == null) {
            throw new IOException("Cannot find FXML file: " + fxmlFile);
        }

        FXMLLoader loader = new FXMLLoader(fxmlUrl);
        Scene scene = new Scene(loader.load(), width, height);
        return scene;
    }

    /**
     * Configures the primary stage with title, scene, and constraints
     * 
     * @param stage The primary stage to configure
     * @param scene The scene to set
     */
    private void configurePrimaryStage(Stage stage, Scene scene) {
        stage.setTitle(APP_TITLE + " v" + APP_VERSION);
        stage.setScene(scene);
        stage.setMinWidth(MIN_WIDTH);
        stage.setMinHeight(MIN_HEIGHT);
        stage.centerOnScreen();

        // Set application icon if available
        // stage.getIcons().add(new Image(Objects.requireNonNull(
        // getResource("icons/app-icon.png")).toExternalForm()));
    }

    /**
     * Helper method to get resource URL
     * 
     * @param resourcePath The resource path
     * @return URL of the resource or null if not found
     */
    private URL getResource(String resourcePath) {
        return StartApplication.class.getResource(resourcePath);
    }

    /**
     * Application entry point
     * 
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Returns the application title
     * 
     * @return Application title string
     */
    public static String getAppTitle() {
        return APP_TITLE;
    }

    /**
     * Returns the application version
     * 
     * @return Application version string
     */
    public static String getAppVersion() {
        return APP_VERSION;
    }
}
