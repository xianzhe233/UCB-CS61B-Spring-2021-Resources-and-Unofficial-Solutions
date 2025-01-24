package gitlet;

import static gitlet.Utils.*;

import java.io.File;
import java.io.Serializable;
import java.util.*;
import java.text.SimpleDateFormat;

/** Represents a gitlet commit object.
 *
 *  @author xianzhe233
 */
public class Commit implements Serializable {
    /**
     *
     * List all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     */

    /** The message of this Commit. */
    static final File COMMITS_DIR = join(Repository.GITLET_DIR, "commits");
    static final long INITIAL_TIMESTAMP = 0;

    private String id;
    private long timestamp;
    private String message;
    private HashMap<String, String> fileMap; // Maps file names to blobs
    String parent = null; // first parent
    String mergedParent = null; // merged parent (if exists)

    /** Return a timestamp for now. */
    static long getTimeStamp() {
        return new Date().getTime();
    }

    /** Converts a timestamp to formated date message string. */
    static String dateOf(long timestamp) {
        Date date = new Date(timestamp);
        SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy Z", Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("GMT-08:00"));
        String formattedDate = sdf.format(date);
        return formattedDate;
    }

    /** Creates a commit object and save it. */
    public Commit(Commit parent, Commit merged, String message, HashMap<String, String> addition, HashSet<String> removal) {
        // Assigns message and timestamp.
        this.message = message;
        this.timestamp = (parent != null) ? getTimeStamp() : INITIAL_TIMESTAMP;

        // Processes parent issues. If parent is not null, copy its fileMap.
        if (parent != null) {
            this.parent = parent.id;
            this.fileMap = new HashMap<>(parent.fileMap);
        } else {
            this.fileMap = new HashMap<>();
        }
        if (merged != null) {
            this.mergedParent = merged.id;
        }

        // Updates with staging area.
        this.fileMap.putAll(addition);
        this.fileMap.entrySet().removeIf(entry -> removal.contains(entry.getKey()));

        // Finally uses hash.
        String parentStr = parent == null? "" : parent.id;
        String mergedStr = merged == null? "" : merged.id;
        this.id = sha1(this.message, String.valueOf(this.timestamp), this.fileMap.toString(), parentStr, mergedStr);

        // Saves the commit as file.
        saveCommit(this);
    }

    /** Saves a commit into the proper file. */
    static void saveCommit(Commit commit) {
        String commitIdHead = commit.id.substring(0, 2);
        String commitIdTail = commit.id.substring(2);
        File headDir = join(COMMITS_DIR, commitIdHead);
        File commitFile = join(headDir, commitIdTail);

        if (!headDir.exists()) {
            headDir.mkdirs();
        }
        createFile(commitFile);
        writeObject(commitFile, commit);
    }

    /** Returns a fixed initial commit. Uses for initializing Gitlet repository. */
    static Commit getInitialCommit() {
        return new Commit(null, null, "initial commit", new HashMap<>(), new HashSet<>());
    }

    /** Returns if this commit contains a file with fileName. */
    boolean contains(String fileName) {
        return this.fileMap.containsKey(fileName);
    }

    /** Gets file of the blob of fileName in this commit. */
    File getFile(String fileName) {
        String blobId = fileMap.get(fileName);
        File file = Blob.get(blobId);
        return file;
    }




    /** Displays commit message for debugging. */
    @Override
    public String toString() {
        return "id: " + id + " timestamp: " + timestamp + " message: " + message;
    }
}
