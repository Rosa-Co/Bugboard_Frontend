package com.unina.bugboardapp.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Issue {

    public enum IssueType {
        QUESTION("Question"),
        BUG("Bug"),
        DOCUMENTATION("Documentation"),
        FEATURE("Feature");

        private final String label;

        IssueType(String label) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }

        @Override
        public String toString() {
            return label;
        }
    }

    public enum Priority {
        LOW("Low"),
        MEDIUM("Medium"),
        HIGH("High");

        private final String label;

        Priority(String label) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }

        @Override
        public String toString() {
            return label;
        }
    }

    public enum IssueState {
        TODO("To Do"),
        IN_PROGRESS("In Progress"),
        DONE("Done");

        private final String label;

        IssueState(String label) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }

        @Override
        public String toString() {
            return label;
        }
    }

    private static int idCounter = 1;

    private final int id;
    private String title;
    private String description;
    private IssueType type;
    private Priority priority;
    private IssueState state;
    private final User reporter;
    private final LocalDateTime createdAt;
    private String imagePath; // Optional
    private final List<Comment> comments;

    public Issue(String title, String description, IssueType type, Priority priority, User reporter) {
        this.id = idCounter++;
        this.title = title;
        this.description = description;
        this.type = type;
        this.priority = priority;
        this.reporter = reporter;
        this.state = IssueState.TODO;
        this.createdAt = LocalDateTime.now();
        this.comments = new ArrayList<>();
    }

    public int getId() {
        return id;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
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

    public void addComment(Comment comment) {
        this.comments.add(comment);
    }
}
