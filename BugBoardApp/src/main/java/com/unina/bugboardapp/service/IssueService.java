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
        }catch (IOException | InterruptedException e){
            Thread.currentThread().interrupt();
            throw new IssueException("Communication error during issue retrieval.", e);
        }catch(Exception e){
            throw new IssueException("Issue retrieval failed: unpredicted error.", e);
        }
    }

    public Issue createIssue(Issue newIssue) throws IssueException {
        try {
            IssueCreateRequest request = new IssueCreateRequest(
                    newIssue.getTitle(),
                    newIssue.getDescription(),
                    newIssue.getType(),
                    newIssue.getImagePath(),
                    newIssue.getPriority(),
                    newIssue.getState());
            String requestBody = mapper.writeValueAsString(request);
            String responseBody = apiClient.post("/issues", requestBody);
            if (responseBody != null && !responseBody.isEmpty()) {
                return mapper.readValue(responseBody, Issue.class);
            }
            throw new IssueException("Issue creation failed: Empty response from server");
        }catch (IOException | InterruptedException e){
            Thread.currentThread().interrupt();
            throw new IssueException("Communication error during issue creation.", e);
        }catch(Exception e){
            throw new IssueException("Issue creation failed: unpredicted error.", e);
        }
    }
}
