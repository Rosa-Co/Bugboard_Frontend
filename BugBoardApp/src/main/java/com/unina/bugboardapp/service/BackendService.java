package com.unina.bugboardapp.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.unina.bugboardapp.model.Comment;
import com.unina.bugboardapp.model.Issue;
import com.unina.bugboardapp.model.User;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class BackendService {

    private static final String BASE_URL = "http://localhost:8080/api";
    private final HttpClient client;
    private final ObjectMapper mapper;

    public BackendService() {
        this.client = HttpClient.newHttpClient();
        this.mapper = new ObjectMapper();
    }

    public List<Issue> fetchAllIssues() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/issues"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            return mapper.readValue(response.body(), new TypeReference<List<Issue>>(){});
        } else {
            throw new RuntimeException("Errore Backend: " + response.statusCode());
        }
    }

    public Issue createIssue(Issue newIssue) throws Exception{
        String json=mapper.writeValueAsString(newIssue);
        HttpRequest request= HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/issues"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        HttpResponse<String> response= client.send(request,HttpResponse.BodyHandlers.ofString());
        return mapper.readValue(response.body(), Issue.class);
    }

    public User createUser(User newUser) throws Exception{
        String json= mapper.writeValueAsString(newUser);
        HttpRequest request= HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/users"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        HttpResponse<String> response= client.send(request,HttpResponse.BodyHandlers.ofString());
        return mapper.readValue(response.body(), User.class);
    }

    public Comment createComment(Comment newComment) throws Exception{
        String json= mapper.writeValueAsString(newComment);
        HttpRequest request= HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/comments"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        HttpResponse<String> response= client.send(request,HttpResponse.BodyHandlers.ofString());
        return mapper.readValue(response.body(),Comment.class);
    }

    public User login(String email, String password) throws Exception {
        String jsonBody = mapper.writeValueAsString(new UserLoginDTO(email, password));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/users/login"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            return mapper.readValue(response.body(), User.class);
        } else {
            return null;
        }
    }

    private static class UserLoginDTO {
        public String email;
        public String password;
        public UserLoginDTO(String e, String p) { this.email = e; this.password = p; }
    }
}