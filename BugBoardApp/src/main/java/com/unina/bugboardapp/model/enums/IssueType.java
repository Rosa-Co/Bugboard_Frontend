package com.unina.bugboardapp.model.enums;
/**
 * Enum che rappresenta la tipologia di una issue.
 * <p>
 * Ogni valore espone una label "user-friendly" (utile in UI) tramite {@link #getLabel()},
 * e sovrascrive {@link #toString()} per restituire direttamente tale label (ad es. in {@code ComboBox}).
 * </p>
 */
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
