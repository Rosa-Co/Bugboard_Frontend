package com.unina.bugboardapp.model;

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
