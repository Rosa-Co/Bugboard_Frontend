package com.unina.bugboardapp.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.unina.bugboardapp.exception.CommentException;
import com.unina.bugboardapp.model.Comment;

import java.io.IOException;
import java.util.List;

public class CommentService {

    private final ApiClient apiClient;
    private final ObjectMapper mapper;

    public CommentService() {
        this.apiClient = ApiClient.getInstance();
        this.mapper = new ObjectMapper();
        this.mapper.registerModule(new JavaTimeModule());
        this.mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    public Comment createComment(Comment newComment) throws CommentException {
        try{
            String requestBody = mapper.writeValueAsString(newComment);
            String responseBody = apiClient.post("/comments", requestBody);
            if (responseBody != null && !responseBody.isEmpty()) {
                return mapper.readValue(responseBody, Comment.class);
            }
            throw new CommentException("Comment creation failed: Empty response from server");
        }catch (IOException | InterruptedException e){
            Thread.currentThread().interrupt();
            throw new CommentException("Communication error during comment creation.", e);
        }catch(Exception e){
            throw new CommentException("Comment creation failed: unpredicted error.", e);
        }
    }

    public List<Comment> getCommentsByIssueId(Integer issueId) throws CommentException {
        try {
            String responseBody = apiClient.get("/comments/issue/" + issueId);
            if (responseBody != null && !responseBody.isEmpty()) {
                return mapper.readValue(responseBody, new TypeReference<List<Comment>>() {
                });
            }
            return List.of();
        }catch (IOException | InterruptedException e){
            Thread.currentThread().interrupt();
            throw new CommentException("Communication error during comment retrieval.", e);
        }catch(Exception e){
            throw new CommentException("Comment retrieval failed: unpredicted error.", e);
        }
    }
}
