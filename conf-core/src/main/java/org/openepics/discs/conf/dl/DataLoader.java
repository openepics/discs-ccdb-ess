package org.openepics.discs.conf.dl;

import java.util.HashMap;
import java.util.List;

import org.openepics.discs.conf.util.IllegalImportFileFormatException;

abstract class DataLoader {

	public static final String CMD_HEADER = "HEADER";
	public static final String CMD_UPDATE = "UPDATE";
	public static final String CMD_DELETE = "DELETE";
	public static final String CMD_RENAME = "RENAME";
	public static final String CMD_END = "END";

	protected HashMap<String, Integer> indexByPropertyName(List<String> fields, List<String> header) {
		final HashMap<String, Integer> indexByPropertyName = new HashMap<>();
		for (String headerEntry : header) {
			if (!fields.contains(headerEntry)) {
				indexByPropertyName.put(headerEntry, header.indexOf(headerEntry));
			}
		}
		return indexByPropertyName;
	}

	protected abstract void setUpIndexesForFields(List<String> header) throws IllegalImportFileFormatException;
}
