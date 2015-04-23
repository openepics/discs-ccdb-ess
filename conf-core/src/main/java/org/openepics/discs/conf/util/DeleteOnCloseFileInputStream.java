/* Taken from stackoverflow
 *
 * http://stackoverflow.com/questions/4693968/is-there-an-existing-fileinputstream-delete-on-close
 */
package org.openepics.discs.conf.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class DeleteOnCloseFileInputStream extends FileInputStream {
    private File file;

    public DeleteOnCloseFileInputStream(File file) throws FileNotFoundException {
        super(file);
        this.file = file;
    }

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
