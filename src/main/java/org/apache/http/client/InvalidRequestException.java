package org.apache.http.client;

import org.apache.http.auth.AuthenticationException;

public class InvalidRequestException extends AuthenticationException {
	private static final long serialVersionUID = -2331577288439065639L;

	/**
     * Creates a new InvalidRequestException with a <tt>null</tt> detail message. 
     */
    public InvalidRequestException() {
        super();
    }

    /**
     * Creates a new InvalidRequestException with the specified message.
     * 
     * @param message the exception detail message
     */
    public InvalidRequestException(String message) {
        super(message);
    }

    /**
     * Creates a new InvalidRequestException with the specified detail message and cause.
     * 
     * @param message the exception detail message
     * @param cause the <tt>Throwable</tt> that caused this exception, or <tt>null</tt>
     * if the cause is unavailable, unknown, or not a <tt>Throwable</tt>
     */
    public InvalidRequestException(String message, Throwable cause) {
        super(message, cause);
    }
}
