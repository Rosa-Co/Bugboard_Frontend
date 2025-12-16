package com.unina.bugboardapp.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Represents a comment on an issue
 */
public class Comment {
    private final User author;
    private final String content;
    private final LocalDateTime timestamp;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm");

    /**
     * Creates a new comment
     * 
     * @param author  The user who wrote the comment
     * @param content The comment text
     */
    public Comment(User author, String content) {
        this.author = author;
        this.content = content;
        this.timestamp = LocalDateTime.now();
    }

    /**
     * Gets the comment author
     * 
     * @return The User who wrote this comment
     */
    public User getAuthor() {
        return author;
    }

    /**
     * Gets the comment content
     * 
     * @return The comment text
     */
    public String getContent() {
        return content;
    }

    /**
     * Gets the comment timestamp
     * 
     * @return The LocalDateTime when the comment was created
     */
    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    /**
     * Returns a formatted timestamp string
     * 
     * @return Formatted timestamp (e.g., "Dec 09, 2025 20:15")
     */
    public String getFormattedTimestamp() {
        return timestamp.format(FORMATTER);
    }

    /**
     * Gets a relative time string (e.g., "2 hours ago")
     * 
     * @return Relative time string
     */
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
