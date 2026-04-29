package com.resumeiq.exception;

public class ScanLimitExceededException extends RuntimeException {
    public ScanLimitExceededException(String message) { super(message); }
}
