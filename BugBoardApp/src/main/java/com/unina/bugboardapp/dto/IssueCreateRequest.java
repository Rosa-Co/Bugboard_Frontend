package com.unina.bugboardapp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.unina.bugboardapp.model.enums.IssueType;
import com.unina.bugboardapp.model.enums.Priority;
import com.unina.bugboardapp.model.enums.IssueState;

public class IssueCreateRequest {
    @JsonProperty("titolo")
    private String title;

    @JsonProperty("descrizione")
    private String description;

    @JsonProperty("tipologia")
    private IssueType type;

    @JsonProperty("img")
    private String imagePath;

    @JsonProperty("priorita")
    private Priority priority;

    @JsonProperty("stato")
    private IssueState state;

    public IssueCreateRequest() {
    }

    public IssueCreateRequest(String title, String description, IssueType type, String imagePath, Priority priority,
            IssueState state) {
        this.title = title;
        this.description = description;
        this.type = type;
        this.imagePath = imagePath;
        this.priority = priority;
        this.state = state;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public IssueType getType() {
        return type;
    }

    public void setType(IssueType type) {
        this.type = type;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public IssueState getState() {
        return state;
    }

    public void setState(IssueState state) {
        this.state = state;
    }
}
