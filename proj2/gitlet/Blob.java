package gitlet;

import java.io.File;

import static gitlet.Utils.*;

public class Blob {
    /**
     * The blobs directory.
     */
    static final File BLOBS_DIR = join(Repository.GITLET_DIR, "blobs");

    /**
     * Returns SHA-1 id of file.
     */
    static String blobHash(File file) {
        String fileContent = readContentsAsString(file);
        return sha1(fileContent);
    }

    /**
     * Creates a blob and returns its id.
     */
    static String createBlob(File file) {
        String id = blobHash(file);
        if (!exists(id)) {
            File blobFile = join(BLOBS_DIR, id);
            createFile(blobFile);
            writeContents(blobFile, readContentsAsString(file));
        }
        return id;
    }

    /**
     * Removes a blob file.
     */
    static void removeBlob(String id) {
        File bolbFile = join(BLOBS_DIR, id);
        if (bolbFile.exists()) {
            bolbFile.delete();
        }
    }

    /**
     * Returns if a blob with id exists.
     */
    static boolean exists(String id) {
        return join(BLOBS_DIR, id).exists();
    }

    /**
     * Gets the blob file by id.
     */
    static File get(String id) {
        return get(BLOBS_DIR, id);
    }

    static File get(File blobDir, String id) {
        File blobFile = join(blobDir, id);
        if (blobFile.exists()) {
            return blobFile;
        } else {
            return null;
        }
    }

    /**
     * Returns if file has the same content as blob.
     */
    static boolean equals(String blobId, File file) {
        String blobContent = readContentsAsString(get(blobId));
        String fileContent = readContentsAsString(file);
        return blobContent.equals(fileContent);
    }

}
