package gitlet;

import java.io.File;
import java.util.HashMap;

import static gitlet.Utils.*;
import static gitlet.Repository.*;

public class Remote {
    /**
     * The file of remote repositories map.
     */
    public static final File REMOTE_FILE = join(GITLET_DIR, "remote");

    /**
     * Initializes remote file.
     */
    static void initRemote() {
        writeObject(REMOTE_FILE, new HashMap<String, String>());
    }

    /**
     * Copies f1's content to f2.
     * former -> latter
     */
    private static void copy(File f1, File f2) {
        if (!f2.exists()) {
            createFile(f2);
        }
        writeContents(f2, readContentsAsString(f1));
    }

    /**
     * Gets all remote repositories.
     */
    static HashMap<String, String> getRemotes() {
        return readObject(REMOTE_FILE, HashMap.class);
    }

    /**
     * Returns if a remote repo named remoteName exists.
     */
    static boolean exists(String remoteName) {
        return getRemotes().containsKey(remoteName);
    }

    /**
     * Returns a remote repository's repo.
     */
    static File remoteRepo(String remoteName) {
        return new File(getRemotes().get(remoteName));
    }

    /**
     * Converts a '/' address to genetic address.
     */
    private static String geneticPath(String path) {
        return path.replace('/', File.separatorChar);
    }

    /**
     * Adds a new remote repository.
     */
    static void remoteAdd(String remoteName, String path) {
        HashMap<String, String> remotes = getRemotes();
        remotes.put(remoteName, geneticPath(path));
        writeObject(REMOTE_FILE, remotes);
    }

    /**
     * Removes a remote repository from remotes.
     */
    static void remoteRemove(String remoteName) {
        HashMap<String, String> remotes = getRemotes();
        remotes.remove(remoteName);
        writeObject(REMOTE_FILE, remotes);
    }

    static File remoteBlobDir(String remoteName) {
        return join(remoteRepo(remoteName), "blobs");
    }

    static File remoteCommitDir(String remoteName) {
        return join(remoteRepo(remoteName), "commits");
    }

    static File remoteBranchDir(String remoteName) {
        return join(remoteRepo(remoteName), "branches");
    }

    /**
     * Gets a blob's file from a remote repo with blobId.
     */
    private static File getBlobFile(String remotename, String blobId) {
        return Blob.get(remoteBlobDir(remotename), blobId);
    }

    /**
     * Gets a commit's file from a remote repo with commitId.
     */
    private static File getCommitFile(String remoteName, String commitId) {
        return Commit.getCommitFile(remoteCommitDir(remoteName), commitId);
    }

    /**
     * Gets a branch's file from a remote repo with branchName.
     */
    private static File getBranchFile(String remoteName, String branch) {
        return Branch.getBranchFile(remoteBranchDir(remoteName), branch);
    }

    /**
     * Returns if a branch named branchName exists in remote repository.
     */
    static boolean hasBranch(String remoteName, String branchName) {
        return getBranchFile(remoteName, branchName).exists();
    }

    /**
     * A getBranch method for remote repository.
     */
    static Commit getBranch(String remoteName, String branchName) {
        return Commit.toCommit(getBranchFile(remoteName, branchName));
    }

    /**
     * A getCommit method for remote repository.
     */
    static Commit getCommit(String remoteName, String commitId) {
        return Commit.toCommit(getCommitFile(remoteName, commitId));
    }

    /**
     * A setBranch method for remote repository.
     */
    static void setBranch(String remoteName, String branchName, String commitId) {
        Branch.set(remoteBranchDir(remoteName), branchName, commitId);
    }

    /**
     * Copies a blob file from fromDir to toDir.
     * Dir format: .../.gitlet/blobs
     */
    static void copyBlob(File fromDir, File toDir, String blobId) {
        File fromFile = Blob.get(fromDir, blobId);
        File toFile = join(toDir, blobId);
        copy(fromFile, toFile);
    }

    /**
     * Copies a commit from fromDir to toDir.
     * Dir format: .../.gitlet
     */
    static void copyCommit(File fromDir, File toDir, String commitId) {
        File fromCommitDir = join(fromDir, "commits");
        File toCommitDir = join(toDir, "commits");
        File fromFile = Commit.getCommitFile(fromCommitDir, commitId);
        Commit commit = Commit.toCommit(fromFile);
        File toSecondaryDir = join(toCommitDir, Commit.idHead(commit.id));
        if (!toSecondaryDir.exists()) {
            toSecondaryDir.mkdirs();
        }
        File toFile = join(toSecondaryDir, Commit.idTail(commit.id));
        copy(fromFile, toFile);

        File fromBlobDir = join(fromDir, "blobs");
        File toBlobDir = join(toDir, "blobs");
        for (String blobId : commit.blobs()) {
            copyBlob(fromBlobDir, toBlobDir, blobId);
        }
    }
}

