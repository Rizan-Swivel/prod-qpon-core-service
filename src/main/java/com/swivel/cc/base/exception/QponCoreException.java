package com.swivel.cc.base.exception;

public class QponCoreException extends RuntimeException {

    /**
     * BaseComponentException Exception with error message.
     *
     * @param errorMessage error message
     */
    public QponCoreException(String errorMessage) {
        super(errorMessage);
    }

    /**
     * Authentication Exception with error message and throwable error
     *
     * @param errorMessage error message
     * @param error        error
     */
    public QponCoreException(String errorMessage, Throwable error) {
        super(errorMessage, error);
    }

}
