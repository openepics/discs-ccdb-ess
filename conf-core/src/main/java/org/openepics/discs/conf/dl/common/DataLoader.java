package org.openepics.discs.conf.dl.common;

import java.util.List;

/**
 * Interface for all data loaders
 *
 * @author Andraz Pozar <andraz.pozar@cosylab.com>
 *
 */
public interface DataLoader {

    /**
     * Saves data read from input file to the database
     *
     * @param inputRows {@link List} of all rows containing data from input file
     * @return {@link DataLoaderResult} describing the outcome of the data loading
     */
    public DataLoaderResult loadDataToDatabase(List<List<String>> inputRows);
}
