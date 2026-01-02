package com.unina.bugboardapp.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.unina.bugboardapp.manager.SessionManager;
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

    public String getValidToken(){
        String token = SessionManager.getInstance().getToken();
        if(token == null || token.isEmpty())
            throw new RuntimeException("User non loggato");
        return token;
    }

    public List<Issue> fetchAllIssues() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/issues"))
                .header("Authorization", "Bearer " + getValidToken())
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            return mapper.readValue(response.body(), new TypeReference<List<Issue>>(){});
        } else {
            handleError(response);
            return null;
        }
    }

    public Issue createIssue(Issue newIssue) throws Exception{
        String token= getValidToken();
        String json=mapper.writeValueAsString(newIssue);
        System.out.println(json);//?debug
        HttpRequest request= HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/issues"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + token)
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        System.out.println(request);//?debug
        HttpResponse<String> response= client.send(request,HttpResponse.BodyHandlers.ofString());
        System.out.println(response.body());//?debug
        if (response.statusCode() != 200 && response.statusCode() != 201) {
            handleError(response);
        }
        return mapper.readValue(response.body(), Issue.class);
    }

    public User createUser(User newUser) throws Exception{
        String token= getValidToken();
        String json= mapper.writeValueAsString(newUser);
        HttpRequest request= HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/users"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + token)
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        HttpResponse<String> response= client.send(request,HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200 && response.statusCode() != 201) {
            handleError(response);
        }
        return mapper.readValue(response.body(), User.class);
    }

    public Comment createComment(Comment newComment) throws Exception{
        String token= getValidToken();
        String json= mapper.writeValueAsString(newComment);
        System.out.println(json);
        HttpRequest request= HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/comments"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + token)
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

    /*public List<Comment> getAllComments(Issue issue) throws Exception{
        HttpRequest request= HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/comments" + "/issues/" + issue.getId() ))
                .GET()
                .build();
        HttpResponse<String> response= client.send(request,HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            System.err.println("ERRORE DAL SERVER: " + response.body());
        }
        ObjectMapper mapper= new ObjectMapper();
        return mapper.readValue(response.body(), new TypeReference<List<Comment>>() {});
    }*/

    public List<Comment> getCommentsByIssueId(Integer issueId) throws Exception{
        String token= getValidToken();
        HttpRequest request= HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/comments" + "/issue/" + issueId ))
                .header("Authorization", "Bearer " + token)
                .GET()
                .build();
        HttpResponse<String> response= client.send(request,HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            System.err.println("ERRORE DAL SERVER: " + response.body());
            throw new RuntimeException("Il server ha risposto con codice (" + response.statusCode() + "): " + response.body());
        }
        return mapper.readValue(response.body(),new TypeReference<List<Comment>>() {});
    }

    public User login(String email, String password) throws Exception {
        UserLoginDTO user= new UserLoginDTO(email,password);
        String jsonBody = mapper.writeValueAsString(user);
        System.out.println(jsonBody);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/auth/login"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();
        System.out.println(request);
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println(response.body());
        if (response.statusCode() == 200) {
            LoginResponseDTO loginResponse = mapper.readValue(response.body(), LoginResponseDTO.class);
            User newUser= new User(email,password,loginResponse.getRole());
            newUser.setId(loginResponse.getUserId());
            SessionManager.getInstance().login(newUser,loginResponse.getToken());
            return newUser;
            //return mapper.readValue(response.body(), User.class);
        } else {
            System.err.println("Errore di autenticazione: " + response.body());
            return null;
        }
    }

    public void handleError(HttpResponse<String> response) throws Exception {
        if (response.statusCode() != 200 && response.statusCode() != 201) {
            System.err.println("ERRORE DAL SERVER: " + response.body());
            throw new RuntimeException("Il server ha risposto con codice (" + response.statusCode() + "): " + response.body());
        }
    }

    public static class UserLoginDTO {
        public String email;
        public String password;
        public UserLoginDTO(String e, String p) { this.email = e; this.password = p; }
    }//TODO sposta in un altro package
    public static class LoginResponseDTO {
        private String token;
        @JsonProperty("id")
        private Integer userId;
        private String type;

        private String email;
        private List<String> roles;

        public LoginResponseDTO() {}

        public String getToken() { return token; }
        @JsonProperty("id")
        public Integer getUserId() { return userId; }
        public String getType() { return type; }
        public String getEmail() { return email; }
        public List<String> getRoles() { return roles; }
        public String getRole(){
            if(getRoles().getFirst().contains("ADMIN")) return "ADMIN";
            return "USER";
        }
        public void setToken(String token) { this.token = token; }
        public void setUserId(Integer userId) { this.userId = userId; }
        public void setType(String type) { this.type = type; }
        public void setEmail(String email) { this.email = email; }
        public void setRoles(List<String> roles) { this.roles = roles; }
    }//TODO sposta in un altro package
}