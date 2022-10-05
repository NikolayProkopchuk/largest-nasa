package com.prokopchuk.largestnasa.exception;

public class WrongUrlException extends RuntimeException {
    public WrongUrlException(String message) {
        super(message);
    }
}
