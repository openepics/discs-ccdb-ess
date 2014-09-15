/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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

    private String blobStoreRoot = "/var/confmgr";

    // is the blob store valid?
    private boolean validStore = true;

    @Inject private AppProperties appProps;

    public BlobStore() { }

    /**
     * Initializes the {@link BlobStore} bean
     *
     */
    @PostConstruct
    public void init() {
        // Todo: either move it to AppProperties or inject it
        final String storeRoot = appProps.getProperty("BlobStoreRoot");

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
        byte[] buffer = new byte[1024];

        while ((len = is.read(buffer)) > 0) {
            os.write(buffer, 0, len);
        }
    }

    private String newBlobId() {
        String fileName = UUID.randomUUID().toString();
        Calendar now = Calendar.getInstance();
        int year = now.get(Calendar.YEAR);
        int month = now.get(Calendar.MONTH) + 1; // zero based
        int day = now.get(Calendar.DAY_OF_MONTH);

        String separator = File.separator;

        String dirName = year + separator + month + separator + day;
        String pathName = blobStoreRoot + separator + dirName;

        File folder = new File(pathName);
        if (!folder.exists() && !folder.mkdirs()) {
            LOGGER.log(Level.SEVERE, "Could not create blob directory {0}", pathName);
            return null;
        }

        String blobId = dirName + separator + fileName;
        String fullPath = blobStoreRoot + separator + blobId;
        File newFile = new File(fullPath);
        if (newFile.exists()) {
            LOGGER.log(Level.SEVERE, "Blob already exists! Name collision {0}", fullPath);
            return null;
        }

        LOGGER.log(Level.INFO, "New Blob Id {0}", blobId);

        return blobId;
    }

    /**
     * Stores a file in the Blob Store
     *
     * @param istream A stream of data to be saved
     * @return an identifier for the stored file
     * @throws IOException
     */
    public String storeFile(InputStream istream) throws IOException {
        String fileId =  this.newBlobId();

        if (istream == null) {
            throw new IOException("istream is null");
        }

        OutputStream ostream = null;
        try {
            ostream = new FileOutputStream( new File(blobStoreRoot + File.separator + fileId) );
            copyFile(istream, ostream);
        } finally {
            if (ostream != null) {
                ostream.close();
            }
        }

        return fileId;
    }

    /**
     * Retrieves a file form the Blob Store as a {@link InputStream}
     *
     * @param fileId identifier of the file as returned by {@link BlobStore#storeFile(InputStream)}
     * @return an {@link InputStream} representing the file conents
     * @throws IOException
     */
    public InputStream retreiveFile(String fileId) throws IOException {
        InputStream istream;

        istream = new FileInputStream(blobStoreRoot + File.separator + fileId);

        return istream;
    }

    /**
     * Deletes a file from the Blob Store
     *
     * @param fileId identifier of the file as returned by {@link BlobStore#storeFile(InputStream)}
     * @throws IOException
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
     * Checks whether the Blob Store is valid (path is accessable)
     * @return <code>true</code> if the path is accessable / blob store is valid
     */
    public boolean isValidStore() {
        return validStore;
    }
}
