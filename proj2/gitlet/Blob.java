package gitlet;
import java.io.File;
import java.io.IOException;

import static gitlet.Utils.*;

public class Blob {
    /** The blobs directory. */
    static final File BLOBS_DIR = join(Repository.GITLET_DIR, "blobs");

    /** Returns SHA-1 id of file. */
    static String blobHash(File file) {
        String fileContent = readContentsAsString(file);
        return sha1(fileContent);
    }


    static String createBlob(File file) {
        String id = blobHash(file);
        if (!exists(id)) {
            File blobFile = join(BLOBS_DIR, id);
            createFile(blobFile);
            writeContents(blobFile, readContentsAsString(file));
        }
        return id;
    }

    static void removeBlob(String id) {
        File bolbFile = join(BLOBS_DIR, id);
        if (bolbFile.exists()) {
            bolbFile.delete();
        }
    }

    static boolean exists(String id) {
        return join(BLOBS_DIR, id).exists();
    }

    static File get(String id) {
        File blobFile = join(BLOBS_DIR, id);
        if (blobFile.exists()) {
            return blobFile;
        } else {
            return null;
        }
    }

}
