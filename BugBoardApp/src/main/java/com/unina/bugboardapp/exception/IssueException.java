package com.unina.bugboardapp.exception;

public class IssueException extends Exception {
    public IssueException(String message) {
        super(message);
    }
    public IssueException(String message, Throwable cause) {
        super(message, cause);
    }
}