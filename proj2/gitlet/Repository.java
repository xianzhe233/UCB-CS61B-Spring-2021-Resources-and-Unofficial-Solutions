package gitlet;

import java.io.File;
import java.util.*;

import static gitlet.Utils.*;

/**
 * Represents a gitlet repository.
 * Repository class maintains staging-area and methods to access working-directory,
 * manipulates blobs, commits, head, branches to provide general ways to work
 * with the repository.
 *
 * @author xianzhe233
 */
public class Repository {

    /**
     * The current working directory.
     */
//    public static final File CWD = new File(System.getProperty("user.dir"));
// TODO: Change CWD back finally.
    public static final File CWD = new File("playground");
    /**
     * The .gitlet directory.
     */
    public static final File GITLET_DIR = join(CWD, ".gitlet");
    /**
     * The stating area directory.
     */
    private static final File STAGING_DIR = join(GITLET_DIR, "stagingArea");
    private static final File ADDITION_FILE = new File(STAGING_DIR, "addition");
    private static final File REMOVAL_FILE = new File(STAGING_DIR, "removal");
    /**
     * Head file.
     */
    private static final File HEAD_FILE = new File(GITLET_DIR, "head");

    /**
     * build gitlet system in CWD.
     */
    public static void initRepository() {
        GITLET_DIR.mkdirs();
        Blob.BLOBS_DIR.mkdirs();
        Branch.BRANCHES_DIR.mkdirs();
        createFile(HEAD_FILE);
        STAGING_DIR.mkdirs();
        createFile(ADDITION_FILE);
        createFile(REMOVAL_FILE);
        clearStagingArea();
        Branch.set(Branch.DEFAULT_BRANCH, Commit.getInitialCommit());
        setHead(Branch.DEFAULT_BRANCH);
    }

    /**
     * Return if gitlet system exists in CWD.
     */
    static boolean exists() {
        return GITLET_DIR.exists();
    }

    /**
     * Returns a file with name fileName in CWD.
     */
    static File fileOf(String fileName) {
        return join(CWD, fileName);
    }

    /**
     * Returns if a file with name fileName exist.
     */
    static boolean isFileExist(String fileName) {
        return fileOf(fileName).exists();
    }

    /**
     * Gets the commit that is pointed by head.
     */
    static Commit getHead() {
        String branchName = readContentsAsString(HEAD_FILE);
        return Branch.get(branchName);
    }

    /**
     * Sets head to a specific branch.
     */
    static void setHead(String branchName) {
        writeContents(HEAD_FILE, branchName);
    }

    /**
     * Gets branch name that head is on.
     */
    static String getBranch() {
        return readContentsAsString(HEAD_FILE);
    }

    /**
     * Returns if the staging area is empty.
     */
    static boolean isStagingAreaEmpty() {
        HashMap<String, String> addition = getAddition();
        HashSet<String> removal = getRemoval();
        List<String> allFiles = workingDirectoryFiles();
        HashSet<String> modified = new HashSet<>(modifiedFiles(allFiles));
        HashSet<String> untracked = new HashSet<>(untrackedFiles(allFiles));
        addition.keySet().removeAll(untracked);
        addition.keySet().removeAll(removal);
        removal.removeAll(untracked);
        modified.removeAll(untracked);
        return addition.isEmpty() && removal.isEmpty();
    }

    /**
     * Gets the HashMap of addition information from file.
     */
    static HashMap<String, String> getAddition() {
        return readObject(ADDITION_FILE, HashMap.class);
    }

    /**
     * Gets the HashSet of removal information from file.
     */
    static HashSet<String> getRemoval() {
        return readObject(REMOVAL_FILE, HashSet.class);
    }

    /**
     * Removes a specific file from addition stage.
     */
    static void removeFromAddition(String fileName) {
        HashMap<String, String> addition = getAddition();
        addition.remove(fileName);
        writeObject(ADDITION_FILE, addition);
    }

    /**
     * Stages a file for addition, saves the file-blob pair.
     * File of fileName not always exists, need to be checked.
     */
    static void stagingAdd(String fileName) {
        HashMap<String, String> addition = getAddition();
        String blobId = Blob.createBlob(fileOf(fileName));
        addition.put(fileName, blobId);
        writeObject(ADDITION_FILE, addition);
    }

    /**
     * Stages a file for removal, saves the fileName in removal file.
     */
    static void stagingRemove(String fileName) {
        HashSet<String> removal = getRemoval();
        removal.add(fileName);
        writeObject(REMOVAL_FILE, removal);
    }

    /**
     * Gets all names of files in the working directory.
     */
    static List<String> workingDirectoryFiles() {
        return plainFilenamesIn(CWD);
    }

    /**
     * Gets all untracked files in the list.
     */
    static List<String> untrackedFiles(List<String> files) {
        Commit currentCommit = getHead();
        HashMap<String, String> addition = getAddition();
        HashSet<String> removal = getRemoval();
        List<String> untrackedFiles = new LinkedList<>();
        for (String fileName : files) {
            /** Untracked files:
             *  1. Untracked in current commit, unstaged for addition now;
             *  2. Staged for removal now, but recreated (still in working directory).
             */
            if ((!currentCommit.contains(fileName) && !addition.containsKey(fileName))
                    || removal.contains(fileName)) {
                untrackedFiles.add(fileName);
            }
        }
        return untrackedFiles;
    }

    /**
     * Returns if a file is in addition.
     */
    static boolean isStagedAddition(String fileName) {
        return getAddition().containsKey(fileName);
    }

    /**
     * Returns if a file is in removal.
     */
    static boolean isStagedRemoval(String fileName) {
        return getRemoval().contains(fileName);
    }

    /**
     * Returns if a file with name fileName in the working directory is different from commit's.
     */
    static boolean different(Commit commit, String fileName) {
        File commitFile = commit.getFile(fileName);
        String CFContent = readContentsAsString(commitFile);
        String WFContent = readContentsAsString(fileOf(fileName));
        return !CFContent.equals(WFContent);
    }

    /**
     * Returns if a file in working directory is different from staged version.
     * Check if file is staged before this.
     */
    private static boolean differentFromAddition(String fileName) {
        String blobId = getAddition().get(fileName);
        return !Blob.equals(blobId, fileOf(fileName));
    }

    /**
     * Gets all modified but not staged files in the list.
     */
    static List<String> modifiedFiles(List<String> files) {
        HashMap<String, String> addition = getAddition();
        HashSet<String> removal = getRemoval();
        Commit currentCommit = getHead();
        HashSet<String> allFiles = new HashSet<>(files);
        allFiles.addAll(addition.keySet());
        allFiles.addAll(currentCommit.files());
        List<String> modifiedFiles = new LinkedList<>();
        for (String fileName : allFiles) {
            /** Modifications Not Staged:
             *  1. Tracked in current commit, changed, but not staged;
             *  2. Staged for addition, but now different from staged version;
             *  3. Staged for addition, but removed in working directory;
             *  4. Tracked in current commit, not staged for removal, but deleted.
             */
            File file = fileOf(fileName);
            if ((currentCommit.contains(fileName) && file.exists() && currentCommit.isChanged(file) && !addition.containsKey(fileName))
                    || (addition.containsKey(fileName) && !file.exists())
                    || (currentCommit.contains(fileName) && !removal.contains(fileName) && !file.exists())
                    || (addition.containsKey(fileName) && file.exists() && differentFromAddition(fileName))
            ) {
                modifiedFiles.add(fileName);
            }
        }
        return modifiedFiles;
    }

    /**
     * Clear the staging area.
     */
    static void clearStagingArea() {
        writeObject(ADDITION_FILE, new HashMap<String, String>());
        writeObject(REMOVAL_FILE, new HashSet<String>());
    }

    /**
     * An integrated commit method.
     */
    static void commit(String message, Commit merged) {
        Commit newCommit = new Commit(getHead(), merged, message, getAddition(), getRemoval());
        clearStagingArea();
        Branch.set(getBranch(), newCommit);
    }

    /**
     * Checks out a file from commit.
     * Need to check if that file is tracked in commit before.
     * This will overwrite the file with same name in working directory.
     */
    static void checkout(Commit commit, String fileName) {
        File blob = commit.getFile(fileName);
        writeContents(fileOf(fileName), readContentsAsString(blob));
    }
}
