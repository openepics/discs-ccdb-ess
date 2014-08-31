/**
 * Interface for file readers. This is implemented so that functionality can be
 * easily expanded to other import formats.
 *
 */
package org.openepics.discs.conf.dl.common;

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
     *
     * @return A {@link List} of {@link List} of {@link String} representing the table-cells from the read import stream
     */
    public List<List<String>> importExcelFile(InputStream inputStream);

}
