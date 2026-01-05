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

    public java.io.InputStream getStream(String endpoint) throws IOException, InterruptedException {
        HttpRequest request = createGetRequest(endpoint);
        HttpResponse<java.io.InputStream> response = client.send(request, HttpResponse.BodyHandlers.ofInputStream());
        if (response.statusCode() >= 400) {
            logger.log(Level.WARNING, () -> "API Error " + response.statusCode());
            throw new ApiException(response.statusCode(), "API call failed for stream");
        }
        return response.body();
    }

    public String postMultipart(String endpoint, Integer id, java.nio.file.Path file)
            throws IOException, InterruptedException {
        String boundary = "---" + System.currentTimeMillis();
        HttpRequest.BodyPublisher bodyPublisher = buildMultipartBody(file, boundary);

        HttpRequest.Builder builder = getBaseRequestBuilder(endpoint)
                .header("Content-Type", "multipart/form-data; boundary=" + boundary)
                .POST(bodyPublisher);

        return executeRequest(builder.build());
    }

    private HttpRequest.BodyPublisher buildMultipartBody(java.nio.file.Path file, String boundary) throws IOException {
        var byteArrays = new java.util.ArrayList<byte[]>();
        String separator = "--" + boundary + "\r\nContent-Disposition: form-data; name=\"file\"; filename=\""
                + file.getFileName() + "\"\r\nContent-Type: " + java.nio.file.Files.probeContentType(file) + "\r\n\r\n";
        byteArrays.add(separator.getBytes(java.nio.charset.StandardCharsets.UTF_8));
        byteArrays.add(java.nio.file.Files.readAllBytes(file));
        byteArrays.add(("\r\n--" + boundary + "--\r\n").getBytes(java.nio.charset.StandardCharsets.UTF_8));
        return HttpRequest.BodyPublishers.ofByteArrays(byteArrays);
    }

    // --- Specific Request Creators ---

    private HttpRequest createGetRequest(String endpoint) {
        return getBaseRequestBuilder(endpoint)
                .GET()
                .build();
    }

    private HttpRequest createPostRequest(String endpoint, String jsonBody) {
        return getBaseRequestBuilder(endpoint)
                .header("Content-Type", "application/json")
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
        // Content-Type application/json is set in getBaseRequestBuilder by default in
        // original code
        // We need to be careful not to set it for multipart if it's already set.
        // Let's modify getBaseRequestBuilder to NOT set Content-Type by default or
        // override it.
        // Checking the original code:
        // 68: builder.header("Content-Type", "application/json");
        // I should remove this line from getBaseRequestBuilder and add it to
        // createPostRequest/createGetRequest (if needed) or handle it flexibly.

        // However, since I am replacing the file content, I can just not include the
        // Content-Type header in getBaseRequestBuilder
        // and add it in createPostRequest and postMultipart manually.
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
            throw new ApiException(response.statusCode(), "API call failed: " + response.body());
        }
    }
}
