package com.hw3.integration.exception;

public class RemoteSystemException extends RuntimeException {

    public RemoteSystemException(String message) {
        super(message);
    }

    public RemoteSystemException(String message, Throwable cause) {
        super(message, cause);
    }
}
