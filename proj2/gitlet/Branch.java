package gitlet;

import java.io.File;
import java.util.List;

import static gitlet.Utils.*;

/**
 * Branch class represents named pointers of specific commits.
 * Branches' files are named as their branch-name, each of them
 * only contains one id of the commit it points at.
 */
public class Branch {
    static final File BRANCHES_DIR = join(Repository.GITLET_DIR, "branches");
    static final String DEFAULT_BRANCH = "master";

    /**
     * Returns branch file of branchName. Branch file don't always exist.
     */
    private static File getBranchFile(String branchName) {
        return getBranchFile(BRANCHES_DIR, branchName);
    }

    static File getBranchFile(File branchDir, String branchName) {
        return join(branchDir, branchName);
    }

    /**
     * Returns if a branch with name branchName exists.
     */
    static boolean exists(String branchName) {
        return exists(BRANCHES_DIR, branchName);
    }

    static boolean exists(File branchDir, String branchName) {
        return getBranchFile(branchDir, branchName).exists();
    }

    /**
     * Gets the commit that is pointed by the branch with name branchName.
     * Always use exists() before this.
     */
    static Commit get(String branchName) {
        return get(BRANCHES_DIR, Commit.COMMITS_DIR, branchName);
    }

    static Commit get(File branchDir, File commitDir, String branchName) {
        String commitId = readContentsAsString(getBranchFile(branchDir, branchName));
        return Commit.toCommit(Commit.getCommitFile(commitDir, commitId));
    }

    /**
     * Set branch with name branchName to point at commit.
     */
    static void set(String branchName, Commit commit) {
        set(BRANCHES_DIR, branchName, commit);
    }

    static void set(String branchName, String commitId) {
        set(BRANCHES_DIR, branchName, commitId);
    }

    static void set(File branchDir, String branchName, Commit commit) {
        set(branchDir, branchName, commit.id);
    }

    static void set(File branchDir, String branchName, String commitId) {
        File branchFile = getBranchFile(branchDir, branchName);
        if (!branchFile.getParentFile().exists()) {
            branchFile.getParentFile().mkdirs();
        }
        if (!branchFile.exists()) {
            createFile(branchFile);
        }
        writeContents(branchFile, commitId);
    }

    /**
     * Removes the branch with name branchName.
     * Use exists() before this.
     */
    static void remove(String branchName) {
        getBranchFile(branchName).delete();
    }

    /**
     * Return names of all branches.
     */
    static List<String> allBranches() {
        return plainFilenamesIn(BRANCHES_DIR);
    }
}
