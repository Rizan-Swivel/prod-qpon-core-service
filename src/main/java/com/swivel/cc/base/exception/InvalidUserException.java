package com.swivel.cc.base.exception;

/**
 * InvalidUserException
 */
public class InvalidUserException extends QponCoreException {
    public InvalidUserException(String errorMessage) {
        super(errorMessage);
    }

    public InvalidUserException(String errorMessage, Throwable error) {
        super(errorMessage, error);
    }
}