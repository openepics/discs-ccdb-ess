/*
 * Copyright (c) 2014 European Spallation Source
 * Copyright (c) 2014 Cosylab d.d.
 *
 * This file is part of Controls Configuration Database.
 *
 * Controls Configuration Database is free software: you can redistribute it
 * and/or modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 2 of the License,
 * or any newer version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see https://www.gnu.org/licenses/gpl-2.0.txt
 */
package org.openepics.discs.conf.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.inject.Inject;

/**
 *
 * @author vuppala
 */
@Stateless
public class BlobStore {
    private static final Logger LOGGER = Logger.getLogger(BlobStore.class.getCanonicalName());
    private static final String BLOB_STORE_PROPERTY_NAME = "BlobStoreRoot";

    private String blobStoreRoot = "/var/confmgr";

    // is the blob store valid?
    private boolean validStore = true;

    @Inject private AppProperties appProps;

    public BlobStore() { }

    /** Initializes the {@link BlobStore} bean */
    @PostConstruct
    public void init() {
        final String storeRoot = appProps.getProperty(BLOB_STORE_PROPERTY_NAME);

        if (storeRoot != null && !storeRoot.isEmpty()) {
            blobStoreRoot = storeRoot;
        }

        final File folder = new File(blobStoreRoot);
        if (!folder.exists() && !folder.mkdirs()) {
            LOGGER.log(Level.SEVERE, "Could not create blob store root {0}", blobStoreRoot);
            validStore = false;
        }
    }

    private void copyFile(InputStream is, OutputStream os) throws IOException {
        int len;
        final byte[] buffer = new byte[8192];

        while ((len = is.read(buffer)) > 0) {
            os.write(buffer, 0, len);
        }
    }

    private String newBlobId() throws IOException {
        final String fileName = UUID.randomUUID().toString();
        final Calendar now = Calendar.getInstance();
        final int year = now.get(Calendar.YEAR);
        final int month = now.get(Calendar.MONTH) + 1; // zero based
        final int day = now.get(Calendar.DAY_OF_MONTH);

        final String separator = File.separator;

        final String dirName = year + separator + month + separator + day;
        final String pathName = blobStoreRoot + separator + dirName;

        final File folder = new File(pathName);
        if (!folder.exists() && !folder.mkdirs()) {
            final String errorMsg = "Could not create blob directory: " + pathName;
            LOGGER.log(Level.SEVERE, errorMsg);
            throw new IOException(errorMsg);
        }

        final String blobId = dirName + separator + fileName;
        final String fullPath = blobStoreRoot + separator + blobId;
        final File newFile = new File(fullPath);
        if (newFile.exists()) {
            final String errorMsg = "Blob already exists! Name collision: " + fullPath;
            LOGGER.log(Level.SEVERE, errorMsg);
            throw new IOException(errorMsg);
        }

        LOGGER.log(Level.FINE, "New Blob Id {0}", blobId);

        return blobId;
    }

    /**
     * Stores a file in the Blob Store
     *
     * @param istream A stream of data to be saved
     * @return an identifier for the stored file
     * @throws IOException possible file operation failure
     */
    public String storeFile(InputStream istream) throws IOException {
        final String fileId =  newBlobId();

        if (istream == null) {
            throw new IOException("Blob store: input data stream is null.");
        }

        try (final OutputStream ostream = new FileOutputStream(new File(blobStoreRoot + File.separator + fileId))) {
            copyFile(istream, ostream);
        }

        return fileId;
    }

    /**
     * Retrieves a file form the Blob Store as a {@link InputStream}
     *
     * @param fileId identifier of the file as returned by {@link BlobStore#storeFile(InputStream)}
     * @return an {@link InputStream} representing the file conents
     * @throws IOException possible file operation failure
     */
    public InputStream retreiveFile(String fileId) throws IOException {
        return new FileInputStream(blobStoreRoot + File.separator + fileId);
    }

    /**
     * Deletes a file from the Blob Store
     *
     * @param fileId identifier of the file as returned by {@link BlobStore#storeFile(InputStream)}
     * @throws IOException possible file operation failure
     */
    public void deleteFile(String fileId) throws IOException {
        final File fileToDelete = new File(blobStoreRoot + File.separator + fileId);
        fileToDelete.delete();
    }

    /**
     * Get's the configured root directory of the Blob Store on the file-system
     * @return a {@link String} path-name
     */
    public String getBlobStoreRoot() {
        return blobStoreRoot;
    }

    /**
     * Checks whether the Blob Store is valid (path is accessible)
     * @return <code>true</code> if the path is accessible / blob store is valid
     */
    public boolean isValidStore() {
        return validStore;
    }
}
