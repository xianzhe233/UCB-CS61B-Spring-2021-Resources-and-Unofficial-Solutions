package gitlet;

import java.io.File;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.*;

import static gitlet.Utils.*;

/**
 * Represents a gitlet commit object.
 * Every Commit object contains commit metadata and a set of mapping relation
 * between file-names and blobs. SHA-1 ids can be used to access specific
 * Serialized Commit objects to implement persistence.
 *
 * @author xianzhe233
 */
public class Commit implements Serializable {
    /**
     *
     * List all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     */

    /**
     * The message of this Commit.
     */
    static final File COMMITS_DIR = join(Repository.GITLET_DIR, "commits");
    static final long INITIAL_TIMESTAMP = 0;
    static final String INITIAL_ID = "2bca61509088a86ded75abaf7eb7ff15f331fad6";
    private final long timestamp;
    private final String message;
    private final HashMap<String, String> fileMap; // Maps file names to blobs
    String id;
    String parent = null; // first parent
    String mergedParent = null; // merged parent (if exists)

    /**
     * Creates a commit object and save it.
     */
    public Commit(Commit parent, Commit merged, String message,
                  HashMap<String, String> addition, HashSet<String> removal) {
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
        String parentStr = parent == null ? "" : parent.id;
        String mergedStr = merged == null ? "" : merged.id;
        this.id = sha1(this.message, String.valueOf(this.timestamp),
                this.fileMap.toString(), parentStr, mergedStr);

        // Saves the commit as file.
        saveCommit(this);
    }

    /**
     * Return a timestamp for now.
     */
    static long getTimeStamp() {
        return new Date().getTime();
    }

    /**
     * Converts a timestamp to formated date message string.
     */
    static String dateOf(long timestamp) {
        Date date = new Date(timestamp);
        SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy Z", Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("GMT-08:00"));
        String formattedDate = sdf.format(date);
        return formattedDate;
    }

    /**
     * Returns the first 2 chars of id.
     */
    static String idHead(String id) {
        return id.substring(0, 2);
    }

    /**
     * Returns chars except the first 2 chars of id.
     */
    static String idTail(String id) {
        return id.substring(2);
    }

    /**
     * Saves a commit into the proper file.
     */
    static void saveCommit(Commit commit) {
        File headDir = join(COMMITS_DIR, idHead(commit.id));
        File commitFile = join(headDir, idTail(commit.id));

        if (!headDir.exists()) {
            headDir.mkdirs();
        }
        createFile(commitFile);
        writeObject(commitFile, commit);
    }

    /**
     * Returns a fixed initial commit. Uses for initializing Gitlet repository.
     */
    static Commit getInitialCommit() {
        return new Commit(null, null, "initial commit", new HashMap<>(), new HashSet<>());
    }

    /**
     * Returns if there exists exactly 1 commit with that id.
     */
    static boolean exists(String id) {
        if (id == null || id.length() <= 2) {
            return false;
        }
        String idHead = idHead(id);
        String idTail = idTail(id);
        File secondaryDir = join(COMMITS_DIR, idHead);
        if (!secondaryDir.exists()) {
            return false;
        }
        List<String> commitNames = plainFilenamesIn(secondaryDir);
        long count = commitNames.stream().filter(name -> name.startsWith(idTail)).count();
        return count == 1;
    }

    /**
     * Reads a commit object from commitFile.
     */
    static Commit toCommit(File commitFile) {
        return readObject(commitFile, Commit.class);
    }

    /**
     * Gets Commit object by commit id. Abbreviation supported.
     */
    static Commit get(String id) {
        return toCommit(getCommitFile(COMMITS_DIR, id));
    }

    static Commit get(File commitDir, String id) {
        return toCommit(getCommitFile(commitDir, id));
    }

    static File getCommitFile(File commitDir, String id) {
        File secondaryDir = join(commitDir, idHead(id));
        String idTail = idTail(id);
        List<String> commitNames = plainFilenamesIn(secondaryDir);
        for (String commitName : commitNames) {
            if (commitName.startsWith(idTail)) {
                File commitFile = join(secondaryDir, commitName);
                return commitFile;
            }
        }
        return null; // Because Command always check existence before get, this will never happen.
    }

    private static boolean isInitial(Commit commit) {
        return commit.id.equals(INITIAL_ID);
    }

    /**
     * Prints logs from commit until initial commit.
     */
    static void log(Commit commit) {
        while (commit != null) {
            commit.print();
            if (isInitial(commit)) {
                break;
            }
            commit = get(commit.parent);
        }
    }

    /**
     * Prints logs of all history commits.
     */
    static void globalLog() {
        List<String> historyCommits = getAllCommits();
        for (String commitId : historyCommits) {
            Commit commit = get(commitId);
            commit.print();
        }
    }

    /**
     * Gets all commits from gitlet repository.
     */
    static List<String> getAllCommits() {
        File[] dirs = COMMITS_DIR.listFiles();
        List<String> commits = new ArrayList<>();
        for (File dir : dirs) {
            List<String> tailCommits = plainFilenamesIn(dir);
            for (String commitTail : tailCommits) {
                String dirPath = dir.getAbsolutePath();
                String commitHead = dirPath.substring(dirPath.length() - 2);
                commits.add(commitHead + commitTail);
            }
        }
        return commits;
    }

    /**
     * Gets c1 and c2's splitPoint.
     */
    static Commit splitPoint(Commit c1, Commit c2) {
        if (c1.equals(c2)) {
            return c1;
        }

        HashSet<String> ancestors = ancestorsOf(c1);
        while (c2 != null) {
            if (ancestors.contains(c2.id)) {
                return c2;
            }
            c2 = get(c2.parent);
        }

        return null; // This should never happen.
    }

    /**
     * Returns all ancestors of source commit.(including srcCommit itself)
     */
    static HashSet<String> ancestorsOf(Commit srcCommit) {
        return ancestorsOf(COMMITS_DIR, srcCommit);
    }

    static HashSet<String> ancestorsOf(File commitDir, Commit srcCommit) {
        HashSet<String> ancestors = new HashSet<>();
        Queue<Commit> commitQueue = new LinkedList<>();
        commitQueue.offer(srcCommit);
        while (!commitQueue.isEmpty()) {
            Commit commit = commitQueue.poll();
            ancestors.add(commit.id);
            if (isInitial(commit)) {
                continue;
            }
            if (!ancestors.contains(commit.parent)) {
                commitQueue.offer(get(commitDir, commit.parent));
            }
            if (commit.mergedParent != null && !ancestors.contains(commit.mergedParent)) {
                commitQueue.offer(get(commitDir, commit.mergedParent));
            }
        }
        return ancestors;
    }

    /**
     * Returns if the file is different at commit1 and commit2.
     */
    static boolean different(Commit c1, Commit c2, String fileName) {
        File f1 = c1.getFile(fileName);
        File f2 = c2.getFile(fileName);
        return !readContentsAsString(f1).equals(readContentsAsString(f2));
    }

    String getMessage() {
        return message;
    }

    /**
     * Returns if this commit contains a file with fileName.
     */
    boolean contains(String fileName) {
        return this.fileMap.containsKey(fileName);
    }

    /**
     * Gets file of the blob of fileName in this commit.
     */
    File getFile(String fileName) {
        String blobId = fileMap.get(fileName);
        File file = Blob.get(blobId);
        return file;
    }

    /**
     * Returns true if this commit's id is equals to another commit's.
     */
    boolean equals(Commit commit) {
        return this.id.equals(commit.id);
    }

    /**
     * Prints out this commit's log message.
     */
    private void print() {
        System.out.println("===");
        System.out.println("commit " + this.id);
        if (mergedParent != null) {
            System.out.println("Merge: " + parent.substring(0, 7)
                    + ' ' + mergedParent.substring(0, 7));
        }
        System.out.println("Date: " + dateOf(this.timestamp));
        System.out.println(this.message);
        System.out.println();
    }

    boolean isChanged(File file) {
        String fileName = file.getName();
        File fileOfCommit = getFile(fileName);
        return !readContentsAsString(fileOfCommit).equals(readContentsAsString(file));
    }

    /**
     * Gets all files that are contained in this commit.
     */
    HashSet<String> files() {
        HashSet<String> files = new HashSet<>(fileMap.keySet());
        return files;
    }

    HashSet<String> blobs() {
        HashSet<String> blobs = new HashSet<>(fileMap.values());
        return blobs;
    }

    /**
     * Displays commit message for debu
     * gging.
     */
    @Override
    public String toString() {
        return "id: " + id + " timestamp: " + timestamp + " message: " + message;
    }
}
