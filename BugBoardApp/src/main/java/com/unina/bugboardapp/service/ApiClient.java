package com.unina.bugboardapp.service;

import com.unina.bugboardapp.exception.ApiException;
import com.unina.bugboardapp.manager.SessionManager;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.logging.Logger;
import java.util.logging.Level;

public class ApiClient {
    private static final Logger logger = Logger.getLogger(ApiClient.class.getName());
    private static final String BASE_URL = "http://localhost:8080/api";
    private static ApiClient instance;
    private final HttpClient client;

    private ApiClient() {
        this.client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
    }

    public static synchronized ApiClient getInstance() {
        if (instance == null) {
            instance = new ApiClient();
        }
        return instance;
    }

    public String get(String endpoint) throws IOException, InterruptedException {
        HttpRequest request = createGetRequest(endpoint);
        return executeRequest(request);
    }

    public String post(String endpoint, String jsonBody) throws IOException, InterruptedException {
        HttpRequest request = createPostRequest(endpoint, jsonBody);
        return executeRequest(request);
    }

    // --- Specific Request Creators ---

    private HttpRequest createGetRequest(String endpoint) {
        return getBaseRequestBuilder(endpoint)
                .GET()
                .build();
    }

    private HttpRequest createPostRequest(String endpoint, String jsonBody) {
        return getBaseRequestBuilder(endpoint)
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();
    }

    // --- Helpers ---

    private HttpRequest.Builder getBaseRequestBuilder(String endpoint) {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + endpoint));

        String token = SessionManager.getInstance().getToken();
        if (token != null && !token.isEmpty()) {
            builder.header("Authorization", "Bearer " + token);
        }
        builder.header("Content-Type", "application/json");
        return builder;
    }

    private String executeRequest(HttpRequest request) throws IOException, InterruptedException {
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        handleError(response);
        return response.body();
    }

    private void handleError(HttpResponse<String> response) {
        if (response.statusCode() >= 400) {
            logger.log(Level.WARNING, () -> "API Error " + response.statusCode() + ": " + response.body());
            throw new ApiException(response.statusCode(),"API call failed: "  + response.body());
        }
    }
}
