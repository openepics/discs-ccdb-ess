package org.openepics.discs.conf.util;

/**
 * An exception signaling that an if/else-if/else or case branch that should be exhaustive encountered an unexpected
 * possibility.
 *
 * @author Marko Kolar <marko.kolar@cosylab.com>
 */
public class UnhandledCaseException extends RuntimeException {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
}
