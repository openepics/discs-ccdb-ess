package org.openepics.discs.conf.util;

public class TimestampParseException extends RuntimeException {

    public TimestampParseException() {
        super();
    }

    public TimestampParseException(String msg) {
        super(msg);
    }

    public TimestampParseException(String msg, Throwable cause) {
        super(msg, cause);
    }

}
