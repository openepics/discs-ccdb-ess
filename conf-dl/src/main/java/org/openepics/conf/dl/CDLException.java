/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.openepics.conf.dl;

/**
 *
 * @author vuppala
 */
public class CDLException extends Exception {
    private CDLExceptionCode code;
    private String message;
    
    CDLException (CDLExceptionCode c) {
        code = c;
        message = c.toString();
    }
    
    CDLException (CDLExceptionCode c, String msg) {
        code = c;
        message = msg;
    }
    
    @Override
    public String getMessage() {
        return code + " " + message;
    }
}
