package com.unina.bugboardapp.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.*;

@JsonIgnoreProperties(ignoreUnknown = true)
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
    @JsonIgnore
    private LocalDateTime createdAt;
    @JsonProperty("img")
    private String imagePath; // Optional
    @JsonIgnore
    private List<Comment> comments;

    public Issue() {
        this.comments = new ArrayList<>();
    }


    public Issue(@JsonProperty("tipologia") String type,@JsonProperty("titolo") String title,@JsonProperty("descrizione") String description,@JsonProperty("img") String img ,@JsonProperty("stato") String state,@JsonProperty("priorita") String priority,@JsonProperty("creataDa") User reporter) throws IllegalArgumentException {
        this.title= title;
        this.description= description;
        this.reporter= reporter;
        this.createdAt= LocalDateTime.now();
        this.comments= new ArrayList<>();
        this.imagePath= img;
        this.type= switch(type.toUpperCase()){
            case "QUESTION" -> IssueType.QUESTION;
            case "BUG" -> IssueType.BUG;
            case "DOCUMENTATION" -> IssueType.DOCUMENTATION;
            case "FEATURE" -> IssueType.FEATURE;
            default -> throw new IllegalArgumentException("Tipo non valido: " + type);
        };
        this.priority= switch(priority.toUpperCase()){
            case "LOW" -> Priority.LOW;
            case "MEDIUM" -> Priority.MEDIUM;
            case "HIGH" -> Priority.HIGH;
            default -> throw new IllegalArgumentException("La priorità " + priority + " non è valida" );
        };
        this.state= switch(state.toUpperCase().replace(" ", "_")){
            case "TODO" -> IssueState.TODO;
            case "IN_PROGRESS" -> IssueState.IN_PROGRESS;
            case "DONE" -> IssueState.DONE;
            default -> throw new IllegalArgumentException("Stato non valido: " + state);
        };
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
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