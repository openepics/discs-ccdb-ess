/* Taken from stackoverflow
 *
 * http://stackoverflow.com/questions/4693968/is-there-an-existing-fileinputstream-delete-on-close
 */
package org.openepics.discs.ccdb.core.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * A class that implements a {@link FileInputStream} that deletes the underlying file once the stream is closed.
 * The primary usage for this class are temporary files.
 * @author <a href="mailto:miha.vitorovic@cosylab.com">Miha Vitorovič</a>
 */
public class DeleteOnCloseFileInputStream extends FileInputStream {
    private File file;

    /**
     * Creates a new stream based on the file object.
     * @param file the file for which to open a stram for
     * @throws FileNotFoundException if the file does not exist
     */
    public DeleteOnCloseFileInputStream(File file) throws FileNotFoundException {
        super(file);
        this.file = file;
    }

    /**
     * Creates a new stream using the file indicated by its name. An absolute file path is recommended.
     * @param filename the name of the file
     * @throws FileNotFoundException if the file does not exist
     */
    public DeleteOnCloseFileInputStream(String filename) throws FileNotFoundException {
        this(new File(filename));
    }

    @Override
    public void close() throws IOException {
        try {
            super.close();
        } finally {
            if (file != null) {
                file.delete();
                file = null;
            }
        }
    }
}
