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
     * @see RuntimeException#RuntimeException(String, Throwable)
     * @param message
     * @param cause
     */
    public CannotAddHeaderRowException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * @see RuntimeException#RuntimeException(String)
     * @param message
     */
    public CannotAddHeaderRowException(String message) {
        super(message);
    }

    /**
     * @see RuntimeException#RuntimeException(Throwable)
     * @param cause
     */
    public CannotAddHeaderRowException(Throwable cause) {
        super(cause);
    }
}
