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
import java.io.Serializable;
import java.util.Calendar;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.inject.Inject;

/**
 *
 * @author vuppala
 */
public class BlobStore implements Serializable {

    private static final Logger logger = Logger.getLogger(BlobStore.class.getName());
    private static String blobStoreRoot = "/var/confmgr";
    private boolean validStore = true; // is the blob store valid?

    @Inject
    private AppProperties appProps;

    public void BlobStore() {
    }

    @PostConstruct
    public void init() {
        String storeRoot = appProps.getProperty("BlobStoreRoot"); // Todo: either move it to AppProperties or inject it

        if (storeRoot != null && !storeRoot.isEmpty()) {
            blobStoreRoot = storeRoot;
        }

        File folder = new File(blobStoreRoot);
        if (!folder.exists()) {
            if (!folder.mkdirs()) {
                logger.log(Level.SEVERE, "Could not create blob store root {0}", blobStoreRoot);
                validStore = false;
            }
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
        if (!folder.exists()) {
            if (!folder.mkdirs()) {
                logger.log(Level.SEVERE, "Could not create blob directory {0}", pathName);
                return null;
            }
        }

        String blobId = dirName + separator + fileName;
        String fullPath = blobStoreRoot + separator + blobId;
        File newFile = new File(fullPath);
        if (newFile.exists()) {
            logger.log(Level.SEVERE, "Blob already exists! Name collision {0}", fullPath);
            return null;
        }
        
        logger.log(Level.INFO, "New Blob Id {0}", blobId);

        return blobId;
    }

    public String storeFile(InputStream istream) throws IOException {
        String fileId =  this.newBlobId();
        OutputStream ostream;

        if (istream == null) {
            throw new IOException("istream is null");
        }

        File ofile = new File(blobStoreRoot + File.separator + fileId);
        ostream = new FileOutputStream(ofile);
        copyFile(istream, ostream);
        // repBean.putFile(folderName, fname, istream);           
        ostream.close();

        return fileId;
    }

    public InputStream retreiveFile(String fileId) throws IOException {
        InputStream istream;

        istream = new FileInputStream(blobStoreRoot + File.separator + fileId);

        return istream;
    }

    public void deleteFile(String fileId) throws IOException {
        InputStream istream;

        // istream = new FileInputStream(blobStoreRoot + fileId);
        // return istream;
    }

    public String getBlobStoreRoot() {
        return blobStoreRoot;
    }

    public boolean isValidStore() {
        return validStore;
    }
}
