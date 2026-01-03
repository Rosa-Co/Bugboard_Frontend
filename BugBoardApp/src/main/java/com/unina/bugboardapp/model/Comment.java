package com.unina.bugboardapp.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Map;

/**
 * Represents a comment on an issue
 */
public class Comment {
    @JsonProperty("id")
    private Integer id;
    @JsonProperty("scrittoDa")
    private User author;
    @JsonProperty("appartieneId")
    private Integer issueId;
    @JsonProperty("descrizione")
    private String content;
    @JsonProperty("data")
    private LocalDateTime timestamp;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm");


    public Comment() {}
    public Comment(User author, String content, Integer issueId) {
        this.author = author;
        this.content = content;
        this.issueId=issueId;
        this.timestamp = LocalDateTime.now();
    }

    @JsonProperty("id")
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }

    @JsonProperty("scrittoDa")
    public User getAuthor() {
        return author;
    }
    public void setAuthor(User author) {
        this.author = author;
    }

    @JsonProperty("appartieneId")
    public Integer getIssueId() {
        return issueId;
    }
    public void setIssue(Integer issueId) {
        this.issueId = issueId;
    }

    @JsonProperty("descrizione")
    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }

    @JsonIgnore
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    @JsonIgnore
    public String getFormattedTimestamp() {
        return timestamp.format(FORMATTER);
    }

    /**
     * Gets a relative time string (e.g., "2 hours ago")
     * 
     * @return Relative time string
     */
    @JsonIgnore
    public String getRelativeTime() {
        LocalDateTime now = LocalDateTime.now();
        java.time.Duration duration = java.time.Duration.between(timestamp, now);
        long minutesAgo = duration.toMinutes();
        long hoursAgo = duration.toHours();
        long daysAgo = duration.toDays();

        if (minutesAgo < 1) {
            return "just now";
        } else if (minutesAgo < 60) {
            return minutesAgo + " minute" + (minutesAgo > 1 ? "s" : "") + " ago";
        } else if (hoursAgo < 24) {
            return hoursAgo + " hour" + (hoursAgo > 1 ? "s" : "") + " ago";
        } else if (daysAgo < 7) {
            return daysAgo + " day" + (daysAgo > 1 ? "s" : "") + " ago";
        } else {
            return getFormattedTimestamp();
        }
    }
}
