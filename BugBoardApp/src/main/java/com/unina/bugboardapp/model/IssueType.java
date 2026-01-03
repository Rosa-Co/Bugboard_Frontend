package com.unina.bugboardapp.model;

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
