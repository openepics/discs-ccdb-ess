/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.openepics.conf.dl;

/**
 *
 * @author vuppala
 */
public interface DataStream {
    public void open(String filename, String sheetname)throws Exception;
    public void nextRow() throws Exception;  // return next record
    public String getColumn(int colNum)  throws Exception ;  // current record
    public int getRowSize() throws Exception; // number of columns in a row
    public boolean endOfStream(); // True if there is no more data 
    public void close() throws Exception;
}
