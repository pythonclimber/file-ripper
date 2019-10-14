package com.ohgnarly.fileripper.exceptions;

public class FileRipperException extends Exception {
    public FileRipperException(String message) {
        super(message);
    }

    public FileRipperException(Throwable cause) {
        super(cause);
    }

    public FileRipperException(String message, Throwable cause) {
        super(message, cause);
    }
}
