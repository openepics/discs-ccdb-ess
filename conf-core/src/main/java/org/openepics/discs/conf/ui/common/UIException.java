package org.openepics.discs.conf.ui.common;

/**
 * A checked exception class to declare UI methods that throw. 
 * Needed to have JSF properly handle exceptional situations.
 * 
 * @author Miroslav Pavleski <miroslav.pavleski@cosylab.com>
 *
 */
public class UIException extends Exception {
    public UIException(String message) {
        super(message);
    }

    public UIException(String message, Throwable cause) {
        super(message, cause);
    }
}
