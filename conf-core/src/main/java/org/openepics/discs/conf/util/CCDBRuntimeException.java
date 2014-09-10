package org.openepics.discs.conf.util;

/**
 * Generic unchecked exception used throught the app..
 *
 * @author Miroslav Pavleski <miroslav.pavleski@cosylab.com>
 */
public class CCDBRuntimeException extends RuntimeException {

    public CCDBRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public CCDBRuntimeException(String message) {
        super(message);
    }

    public CCDBRuntimeException(Throwable cause) {
        super(cause);
    }
}
