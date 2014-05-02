/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.openepics.conf.dl;

import java.util.*;
/**
 *
 * @author vuppala
 */
public class DSHeader {
    private Map<String, Integer> header; // field-name, description
    
    DSHeader() {
        header = new HashMap<String, Integer>();
    }
    
    public int getValue(String name) {
        return header.get(name);
    }
    
    public void setEntry(String name, int value) {
        header.put(name, value);
    }
    
    Map<String, Integer> get() {
        return header;
    }
    
    public void clear() {
        header.clear();
    }
}
