package org.openepics.discs.conf.util;

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
