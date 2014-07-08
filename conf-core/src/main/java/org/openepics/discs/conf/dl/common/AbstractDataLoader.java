package org.openepics.discs.conf.dl.common;

import java.util.HashMap;
import java.util.List;

/**
 * Skeleton for all data loaders.
 *
 * @author Andraz Pozar <andraz.pozar@cosylab.com>
 *
 */
public abstract class AbstractDataLoader {

    public static final String CMD_HEADER = "HEADER";
    public static final String CMD_UPDATE = "UPDATE";
    public static final String CMD_DELETE = "DELETE";
    public static final String CMD_RENAME = "RENAME";
    public static final String CMD_END = "END";

    /**
     * Sets up index for each necessary field.
     *
     * @param header List containing all header row column values
     *
     * @return {@link DataLoaderResult} result of setting up fields indexes
     */
    protected abstract DataLoaderResult setUpIndexesForFields(List<String> header);

    /**
     * Method is used to find indexes of properties from header row. Returned is a map giving property index in the
     * header by property name.
     *
     * @param fields List of all necessary fields. All other columns in header row are considered to be properties.
     * @param header List containing all header row column values
     *
     * @return Property index by property name map of properties in the header
     */
	protected HashMap<String, Integer> indexByPropertyName(List<String> fields, List<String> header) {
		final HashMap<String, Integer> indexByPropertyName = new HashMap<>();
		for (String headerEntry : header) {
			if (!fields.contains(headerEntry) && headerEntry != null && headerEntry.length() > 0) {
				indexByPropertyName.put(headerEntry, header.indexOf(headerEntry));
			}
		}
		return indexByPropertyName;
	}
}
