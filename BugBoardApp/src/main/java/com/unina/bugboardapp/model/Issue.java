package com.unina.bugboardapp.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.*;
import com.unina.bugboardapp.model.enums.IssueState;
import com.unina.bugboardapp.model.enums.IssueType;
import com.unina.bugboardapp.model.enums.Priority;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Issue {

    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    @JsonProperty("id")
    private Integer id;
    @JsonProperty("titolo")
    private String title;
    @JsonProperty("descrizione")
    private String description;
    @JsonProperty("tipologia")
    private IssueType type;
    @JsonProperty("priorita")
    private Priority priority;
    @JsonProperty("stato")
    private IssueState state;
    @JsonProperty("creataDa")
    private User reporter;
    @JsonProperty("img")
    private String imagePath; // Optional
    @JsonIgnore
    private List<Comment> comments;

    public Issue() {
        this.comments = new ArrayList<>();
    }

    public Issue(IssueType type, String title, String description, String imagePath, IssueState state,
            Priority priority, User reporter) {
        this.title = title;
        this.description = description;
        this.reporter = reporter;
        this.comments = new ArrayList<>();
        this.imagePath = imagePath;
        this.type = type;
        this.priority = priority;
        this.state = state;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public IssueType getType() {
        return type;
    }

    public Priority getPriority() {
        return priority;
    }

    public IssueState getState() {
        return state;
    }

    public User getReporter() {
        return reporter;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setType(IssueType type) {
        this.type = type;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public void setState(IssueState state) {
        this.state = state;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public void addComment(Comment comment) {
        if (this.comments == null) {
            this.comments = new ArrayList<>();
        }
        this.comments.add(comment);
    }
}