package com.unina.bugboardapp.utils;

import javafx.concurrent.Task;
import javafx.application.Platform;
import javafx.scene.control.ProgressIndicator;
import java.util.function.Supplier;
import java.util.function.Consumer;

public class AsyncHelper {

    private final ProgressIndicator spinner;

    public AsyncHelper(ProgressIndicator spinner) {
        this.spinner = spinner;
    }

    public <T> void run(Supplier<T> operation, Consumer<T> onDone) {
        // Ensure UI updates happen on the JavaFX Application Thread
        Platform.runLater(() -> spinner.setVisible(true));

        Task<T> task = new Task<>() {
            @Override
            protected T call() {
                return operation.get();
            }
        };

        task.setOnSucceeded(e -> {
            Platform.runLater(() -> spinner.setVisible(false));
            onDone.accept(task.getValue());
        });

        task.setOnFailed(e -> {
            Platform.runLater(() -> spinner.setVisible(false));
            Throwable exception = task.getException();
            System.err.println("AsyncHelper Error: " + exception);
            exception.printStackTrace();
            // Optional: You might want to handle errors more gracefully here, maybe with a
            // callback
        });

        Thread thread = new Thread(task);
        thread.setDaemon(true); // Ensure thread doesn't prevent app exit
        thread.start();
    }
}
