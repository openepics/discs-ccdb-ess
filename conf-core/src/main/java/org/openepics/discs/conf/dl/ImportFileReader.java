/**
 * Interface for file readers. This is implemented so that functionality can be
 * easily expanded to other import formats.
 * 
 */
package org.openepics.discs.conf.dl;

import java.io.InputStream;
import java.util.List;

/**
 * @author Andraz Pozar <andraz.pozar@cosylab.com>
 *
 */
public interface ImportFileReader {
    
    /**
     * Entry point for reader.
     * 
     * @param inputStream {@link InputStream} of import file
     */
    public List<List<String>> importExcelFile(InputStream inputStream);

}
