package org.openepics.discs.conf.auditlog;


/**
 * Converts entity to String dump, with standard format and stripped ID, Name etc.
 * 
 * @author mpavleski
 *
 */
public interface EntityLogger {
    
    /*
     * Returns the type of the handled logger
     */
    public Class getType();
    
    /**
     * Serializes entity value to a String
     * 
     * @param value
     * @return
     */
    public String serializeEntity(Object value);
}
