package com.unina.bugboardapp.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
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
        this.mapper.registerModule(new JavaTimeModule());
        this.mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
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
            throw new RuntimeException("Errore Backend (" + response.statusCode() + "): " + response.body());
        }
    }

    public Issue createIssue(Issue newIssue) throws Exception{
        String json=mapper.writeValueAsString(newIssue);
        System.out.println(json);//debug
        HttpRequest request= HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/issues"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        System.out.println(request);
        HttpResponse<String> response= client.send(request,HttpResponse.BodyHandlers.ofString());
        System.out.println(response.body());
        if (response.statusCode() != 200 && response.statusCode() != 201) {
            System.err.println("ERRORE DAL SERVER: " + response.body());
            throw new RuntimeException("Il server ha risposto con codice (" + response.statusCode() + "): " + response.body());
        }
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
        if (response.statusCode() != 200 && response.statusCode() != 201) {
            System.err.println("ERRORE DAL SERVER: " + response.body());
            throw new RuntimeException("Il server ha risposto con codice (" + response.statusCode() + "): " + response.body());
        }
        return mapper.readValue(response.body(), User.class);
    }

    public Comment createComment(Comment newComment) throws Exception{
        String json= mapper.writeValueAsString(newComment);
        System.out.println(json);
        HttpRequest request= HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/comments"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        System.out.println(request);
        HttpResponse<String> response= client.send(request,HttpResponse.BodyHandlers.ofString());
        System.out.println(response.body());
        if (response.statusCode() != 200 && response.statusCode() != 201) {
            System.err.println("ERRORE DAL SERVER: " + response.body());
            throw new RuntimeException("Il server ha risposto con codice (" + response.statusCode() + "): " + response.body());
        }
        return mapper.readValue(response.body(),Comment.class);
    }

    public List<Comment> getAllComments(Issue issue) throws Exception{
        HttpRequest request= HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/issues/" + issue.getId() + "/comments"))
                .GET()
                .build();
        HttpResponse<String> response= client.send(request,HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            System.err.println("ERRORE DAL SERVER: " + response.body());
        }
        ObjectMapper mapper= new ObjectMapper();
        return mapper.readValue(response.body(), new TypeReference<List<Comment>>() {});
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