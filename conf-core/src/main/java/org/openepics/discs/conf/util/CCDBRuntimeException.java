package org.openepics.discs.conf.util;

/**
 * Generic unchecked exception used throught the app..
 *
 * @author Miroslav Pavleski <miroslav.pavleski@cosylab.com>
 */
public class CCDBRuntimeException extends RuntimeException {

    /**
     * @see RuntimeException#RuntimeException(String, Throwable)
     * @param message
     * @param cause
     */
    public CCDBRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * @see RuntimeException#RuntimeException(String)
     * @param message
     */
    public CCDBRuntimeException(String message) {
        super(message);
    }

    /**
     * @see RuntimeException#RuntimeException(Throwable)
     * @param cause
     */
    public CCDBRuntimeException(Throwable cause) {
        super(cause);
    }
}
