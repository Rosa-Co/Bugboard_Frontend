package com.unina.bugboardapp.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.unina.bugboardapp.exception.IssueException;
import com.unina.bugboardapp.model.Issue;
import com.unina.bugboardapp.dto.IssueCreateRequest;

import java.io.IOException;
import java.util.List;

public class IssueService {

    private final ApiClient apiClient;
    private final ObjectMapper mapper;

    public IssueService() {
        this.apiClient = ApiClient.getInstance();
        this.mapper = new ObjectMapper();
        this.mapper.registerModule(new JavaTimeModule());
        this.mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    public List<Issue> fetchAllIssues() throws IssueException {
        try {
            String responseBody = apiClient.get("/issues");
            if (responseBody != null && !responseBody.isEmpty()) {
                return mapper.readValue(responseBody, new TypeReference<List<Issue>>() {
                });
            }
            return List.of();
        } catch (IOException | InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IssueException("Communication error during issue retrieval.", e);
        } catch (Exception e) {
            throw new IssueException("Issue retrieval failed: unpredicted error.", e);
        }
    }

    public Issue createIssue(Issue newIssue) throws IssueException {
        try {
            mapper.registerModule(new JavaTimeModule());
            String localImagePath = newIssue.getImagePath();

            IssueCreateRequest request = new IssueCreateRequest(
                    newIssue.getTitle(),
                    newIssue.getDescription(),
                    newIssue.getType(),
                    null,
                    newIssue.getPriority(),
                    newIssue.getState());
            String requestBody = mapper.writeValueAsString(request);
            String responseBody = apiClient.post("/issues", requestBody);

            Issue createdIssue;
            if (responseBody != null && !responseBody.isEmpty()) {
                createdIssue = mapper.readValue(responseBody, Issue.class);
            } else {
                throw new IssueException("Issue creation failed: Empty response from server");
            }

            if (localImagePath != null && !localImagePath.isEmpty()) {
                java.nio.file.Path path = java.nio.file.Path.of(localImagePath);
                if (java.nio.file.Files.exists(path)) {
                    String serverPath = apiClient.postMultipart("/images/upload/" + createdIssue.getId(),
                            createdIssue.getId(), path);
                    createdIssue.setImagePath(serverPath);
                }
            }
            return createdIssue;
        } catch (IOException | InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IssueException("Communication error during issue creation.", e);
        } catch (Exception e) {
            throw new IssueException("Issue creation failed: unpredicted error.", e);
        }
    }

    public java.io.InputStream downloadImage(String filename) throws IssueException {
        try {
            // filename comes as "uploads/uuid_name.ext" from backend usually,
            // but our API endpoint is /api/images/{filename}
            // we need to extract just the filename if it contains directory
            String actualFilename = filename;
            if (filename.contains("/") || filename.contains("\\")) {
                actualFilename = new java.io.File(filename).getName();
            }
            return apiClient.getStream("/images/" + actualFilename);
        } catch (IOException | InterruptedException e) {
            throw new IssueException("Error downloading image", e);
        }
    }
}
