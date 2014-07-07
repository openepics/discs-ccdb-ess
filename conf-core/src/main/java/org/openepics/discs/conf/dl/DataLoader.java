package org.openepics.discs.conf.dl;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;

import org.openepics.discs.conf.util.IllegalImportFileFormatException;
import org.openepics.discs.conf.util.NotAuthorizedException;

/**
 * Skeleton for all data loaders.
 *
 * @author Andraz Pozar <andraz.pozar@cosylab.com>
 *
 */
abstract class DataLoader {

	public static final String CMD_HEADER = "HEADER";
	public static final String CMD_UPDATE = "UPDATE";
	public static final String CMD_DELETE = "DELETE";
	public static final String CMD_RENAME = "RENAME";
	public static final String CMD_END = "END";
	/**
	 * Loads data from excel of CSV spreadsheet and checks for possible problems. If there are problems
	 * no import occurs and exception with message describing the problem is thrown.
	 *
	 * @param stream Import file
	 * @throws IllegalImportFileFormatException
	 * @throws NotAuthorizedException
	 */
	public abstract void loadData(InputStream stream) throws IllegalImportFileFormatException, NotAuthorizedException;

	/**
	 * If there were no problems loading the data from import file, this method saves the data to the database.
	 */
	protected abstract void doImport();

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
			if (!fields.contains(headerEntry)) {
				indexByPropertyName.put(headerEntry, header.indexOf(headerEntry));
			}
		}
		return indexByPropertyName;
	}

	/**
	 * Sets up index for each necessary field.
	 *
	 * @param header List containing all header row column values
	 * @throws IllegalImportFileFormatException
	 */
	protected abstract void setUpIndexesForFields(List<String> header) throws IllegalImportFileFormatException;
}
