package com.unina.bugboardapp.model;
/**
 * Enum che rappresenta la priorità di una issue.
 * <p>
 * Ogni valore espone una label "user-friendly" (utile in UI) tramite {@link #getLabel()},
 * e sovrascrive {@link #toString()} per restituire direttamente tale label.
 * </p>
 */
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
