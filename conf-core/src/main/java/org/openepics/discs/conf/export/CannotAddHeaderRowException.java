package org.openepics.discs.conf.export;

public class CannotAddHeaderRowException extends RuntimeException {
    private static final long serialVersionUID = -6959901582547421260L;

    /**
     * @see RuntimeException#RuntimeException()
     */
    public CannotAddHeaderRowException() {
        super();
    }

    /**
     * @param message the message
     * @param cause the cause
     * @see RuntimeException#RuntimeException(String, Throwable)
     */
    public CannotAddHeaderRowException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * @param message the message
     * @see RuntimeException#RuntimeException(String)
     */
    public CannotAddHeaderRowException(String message) {
        super(message);
    }

    /**
     * @param cause the cause
     * @see RuntimeException#RuntimeException(Throwable)
     */
    public CannotAddHeaderRowException(Throwable cause) {
        super(cause);
    }
}
