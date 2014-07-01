package org.openepics.discs.conf.util;

/**
 * This exception should be thrown if there is a problem with import file format.
 * Message of exception gives short description of the problem and file row in which
 * the problem happened.
 * 
 * @author Andraz Pozar <andraz.pozar@cosylab.com>
 *
 */
public class IllegalImportFileFormatException extends Exception{

    private static final long serialVersionUID = 1859728936331082601L;
    private String row;
    private String message;
    
    public IllegalImportFileFormatException(String message, String row) {
        super();
        this.row = row;
        this.message = message;
    }
    
    @Override
    public String getMessage() {
        return "In row " + row + ": " + message;
    }

}
