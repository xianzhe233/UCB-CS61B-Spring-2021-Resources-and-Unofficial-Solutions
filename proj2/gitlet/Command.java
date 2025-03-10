package gitlet;

import java.util.*;

import static gitlet.GitletException.*;
import static gitlet.Repository.*;
import static gitlet.Utils.*;

/**
 * Represents commands of Gitlet.
 */
public class Command {
    /**
     * A map that shows possible arguments numbers of every command.
     */
    private static final Map<String, Set<Integer>> ARGS_MAP = Map.ofEntries(
            Map.entry("init", Set.of(0)),
            Map.entry("add", Set.of(1)),
            Map.entry("commit", Set.of(1)),
            Map.entry("rm", Set.of(1)),
            Map.entry("log", Set.of(0)),
            Map.entry("global-log", Set.of(0)),
            Map.entry("find", Set.of(1)),
            Map.entry("status", Set.of(0)),
            Map.entry("checkout", Set.of(2, 3, 1)),
            Map.entry("branch", Set.of(1)),
            Map.entry("rm-branch", Set.of(1)),
            Map.entry("reset", Set.of(1)),
            Map.entry("merge", Set.of(1)),
            // Extra Credit
            Map.entry("add-remote", Set.of(2)),
            Map.entry("rm-remote", Set.of(1)),
            Map.entry("push", Set.of(2)),
            Map.entry("fetch", Set.of(2)),
            Map.entry("pull", Set.of(2))
    );

    static void process(String[] args) throws GitletException {
        if (args.length == 0) {
            throw noArgsException();
        }

        String command = args[0];
        String[] commandArgs = Arrays.copyOfRange(args, 1, args.length);

        if (!ARGS_MAP.containsKey(command)) {
            throw commandNotExistException();
        }
        if (!ARGS_MAP.get(command).contains(commandArgs.length)) {
            throw operandsIncorrectException();
        }

        if (command.equals("init")) {
            init();
        } else {
            if (!exists()) {
                throw uninitializedException();
            }
            switch (command) {
                case "add":
                    add(commandArgs);
                    break;
                case "commit":
                    commit(commandArgs);
                    break;
                case "rm":
                    rm(commandArgs);
                    break;
                case "log":
                    log();
                    break;
                case "global-log":
                    globalLog();
                    break;
                case "find":
                    find(commandArgs);
                    break;
                case "status":
                    status();
                    break;
                case "checkout":
                    checkout(commandArgs);
                    break;
                case "branch":
                    branch(commandArgs);
                    break;
                case "rm-branch":
                    rmBranch(commandArgs);
                    break;
                case "reset":
                    reset(commandArgs);
                    break;
                case "merge":
                    merge(commandArgs);
                    break;
                case "add-remote":
                    addRemote(commandArgs);
                    break;
                case "rm-remote":
                    rmRemote(commandArgs);
                    break;
                case "push":
                    push(commandArgs);
                    break;
                case "fetch":
                    fetch(commandArgs);
                    break;
                case "pull":
                    pull(commandArgs);
                    break;

                default:
                    throw commandNotExistException();
            }
        }
    }

    private static void init() throws GitletException {
        if (exists()) {
            throw initGitletAlreadyExistsException();
        }
        initRepository();
    }

    private static void add(String[] args) throws GitletException {
        add(args[0]);
    }

    private static void add(String fileName) throws GitletException {
        if (!isFileExist(fileName)) {
            throw addFileNotExistException();
        }

        stagingAdd(fileName);

        Commit currentCommit = getHead();
        if (currentCommit.contains(fileName) && !currentCommit.isChanged(fileOf(fileName))) {
            removeFromAddition(fileName);
            removeFromRemoval(fileName);
        }
        // If new added file's version is the same as currentCommit, cancel addition.
    }

    private static void commit(String[] args) throws GitletException {
        if (isStagingAreaEmpty()) {
            throw commitNoStagedFileException();
        }

        commit(args[0]);
    }

    private static void commit(String message) throws GitletException {
        if (message.isEmpty()) {
            throw commitEmptyMessageException();
        }

        Repository.commit(message, null);
    }

    /**
     * An integrated commit method for merged commit operation.
     */
    private static void mergedCommit(String branchName, String message) throws GitletException {
        if (isStagingAreaEmpty()) {
            throw commitNoStagedFileException();
        }

        Repository.commit(message, branchName);
    }

    private static void rm(String[] args) throws GitletException {
        rm(args[0]);
    }

    private static void rm(String fileName) throws GitletException {
        Commit currentCommit = getHead();
        if (!isStagedAddition(fileName) && !currentCommit.contains(fileName)) {
            throw rmNoReasonToRemoveException();
        }

        if (isStagedAddition(fileName)) {
            removeFromAddition(fileName);
        }
        if (currentCommit.contains(fileName)) {
            stagingRemove(fileName);
            fileOf(fileName).delete();
        }
    }

    private static void log() throws GitletException {
        Commit.log(getHead());
    }

    private static void globalLog() throws GitletException {
        Commit.globalLog();
    }

    private static void find(String[] args) throws GitletException {
        find(args[0]);
    }

    private static void find(String message) throws GitletException {
        List<String> commitIds = Commit.getAllCommits();
        boolean found = false;
        for (String commitId : commitIds) {
            Commit commit = Commit.get(commitId);
            if (commit.getMessage().equals(message)) {
                System.out.println(commit.id);
                found = true;
            }
        }
        if (!found) {
            throw findNoSameMessageException();
        }
    }

    private static void status() throws GitletException {
        List<String> workingDirectoryFiles = workingDirectoryFiles();
        List<String> modifiedFiles = modifiedFiles(workingDirectoryFiles);
        List<String> untrackedFiles = untrackedFiles(workingDirectoryFiles);
        // branches
        System.out.println("=== Branches ===");
        List<String> branches = Branch.allBranches();
        String currentBranch = getBranch();
        System.out.println("*" + currentBranch);
        for (String branch : branches) {
            if (!branch.equals(currentBranch)) {
                System.out.println(branch);
            }
        }
        System.out.println();

        // staged files
        System.out.println("=== Staged Files ===");
        ArrayList<String> stagedFiles = new ArrayList<>(getAddition().keySet());
        Collections.sort(stagedFiles);
        for (String fileName : stagedFiles) {
            if (!fileOf(fileName).exists() || differentFromAddition(fileName)) {
                continue;
            }
            System.out.println(fileName);
        }
        System.out.println();

        // removed files
        System.out.println("=== Removed Files ===");
        ArrayList<String> removedFiles = new ArrayList<>(getRemoval());
        Collections.sort(removedFiles);
        for (String fileName : removedFiles) {
            if (fileOf(fileName).exists()) {
                continue;
            }
            System.out.println(fileName);
        }
        System.out.println();

        // modifications not staged for commit
        System.out.println("=== Modifications Not Staged For Commit ===");
        Collections.sort(modifiedFiles);
        for (String fileName : modifiedFiles) {
            String afterName;
            if (!fileOf(fileName).exists()) {
                afterName = " (deleted)";
            } else {
                afterName = " (modified)";
            }
            System.out.println(fileName + afterName);
        }
        System.out.println();

        // untracked files
        System.out.println("=== Untracked Files ===");
        Collections.sort(untrackedFiles);
        for (String fileName : untrackedFiles) {
            System.out.println(fileName);
        }
        System.out.println();
    }

    private static void checkout(String[] args) throws GitletException {
        switch (args.length) {
            case 1:
                checkout(args[0]);
                break;
            case 2:
                checkout(args[0], args[1]);
                break;
            case 3:
                checkout(args[0], args[1], args[2]);
                break;
            default:
                throw operandsIncorrectException();
        }
    }


    private static void checkout(String dash, String fileName) throws GitletException {
        if (!dash.equals("--")) {
            throw operandsIncorrectException();
        }
        if (!getHead().contains(fileName)) {
            throw checkoutFileNotExistException();
        }

        Repository.checkout(getHead(), fileName);
    }

    private static void checkout(String commitId, String dash, String fileName)
            throws GitletException {
        if (!dash.equals("--")) {
            throw operandsIncorrectException();
        }
        if (!Commit.exists(commitId)) {
            throw checkoutCommitNotExistException();
        }
        Commit commit = Commit.get(commitId);
        if (!commit.contains(fileName)) {
            throw checkoutFileNotExistException();
        }

        Repository.checkout(Commit.get(commitId), fileName);
    }

    private static void checkout(String branchName) throws GitletException {
        if (!Branch.exists(branchName)) {
            throw checkoutBranchNotExistException();
        }
        if (getBranch().equals(branchName)) {
            throw checkoutCurrentBranchException();
        }

        Commit branchCommit = Branch.get(branchName);
        Commit currentCommit = getHead();

        untrackedCheck(branchCommit, checkoutDangerousException());

        setHead(branchName);

        for (String fileName : currentCommit.files()) {
            if (!branchCommit.contains(fileName)) {
                fileOf(fileName).delete();
            }
        }

        for (String fileName : branchCommit.files()) {
            if (!fileOf(fileName).exists()) {
                createFile(fileOf(fileName));
            }
            Repository.checkout(branchCommit, fileName);
        }

        clearStagingArea();
    }

    private static void branch(String[] args) throws GitletException {
        branch(args[0]);
    }

    private static void branch(String branchName) throws GitletException {
        if (Branch.exists(branchName)) {
            throw branchAlreadyExistsException();
        }

        Branch.set(branchName, getHead());
    }

    private static void rmBranch(String[] args) throws GitletException {
        rmBranch(args[0]);
    }

    private static void rmBranch(String branchName) throws GitletException {
        if (!Branch.exists(branchName)) {
            throw rmBranchNotExistException();
        }
        if (branchName.equals(getBranch())) {
            throw rmBranchRemoveCurrentBranchException();
        }

        Branch.remove(branchName);
    }

    private static void reset(String[] args) throws GitletException {
        reset(args[0]);
    }

    /**
     * Throws given exception when an untracked file would be
     * overwritten by checking out commit.
     */
    private static void untrackedCheck(Commit commit, GitletException e) throws GitletException {
        List<String> untrackedFiles = untrackedFiles(workingDirectoryFiles());
        for (String fileName : untrackedFiles) {
            if (commit.contains(fileName)) {
                throw e;
            }
        }
    }

    private static void reset(String commitId) throws GitletException {
        if (!Commit.exists(commitId)) {
            throw resetCommitNotExistException();
        }

        Commit commit = Commit.get(commitId);
        Commit currentCommit = getHead();

        untrackedCheck(commit, resetDangerousException());

        for (String fileName : commit.files()) {
            Repository.checkout(commit, fileName);
        }

        for (String fileName : currentCommit.files()) {
            if (!commit.contains(fileName)) {
                fileOf(fileName).delete();
            }
        }

        Branch.set(getBranch(), commit);
        clearStagingArea();
    }

    private static void merge(String[] args) throws GitletException {
        if (!isStagingAreaEmpty()) {
            throw mergeStagingAreaNotClearException();
        }

        merge(args[0]);
    }

    /**
     * Any files that have been modified in the given branch since the split point,
     * but not modified in the current branch since the split point should be changed
     * to their versions in the given branch and automatically staged.
     */

    /**
     * Any files that have been modified in the current branch but not in the given
     * branch since the split point should stay as they are. (do nothing)
     */

    /**
     * Any files that have been modified in both the current and given branch in the
     * same way (i.e., both files now have the same content or were both removed) are
     * left unchanged by the merge. (do nothing)
     */

    /**
     * Files that didn't present at split point, but only presents at the given branch
     * should be checked out and staged.
     */

    /** Files that presented at split point, unmodified at current branch, doesn't
     *  exist at the given branch should be removed.
     */

    /** Files that presented at split point, unmodified at given branch, doesn't
     *  exist at the current branch should remain absent. (do nothing)
     */

    /**
     * In conflict:
     * 1. both changed since split point, but contents are different;
     * 2. one is changed since split point, another is absent;
     * 3. absent at split point, different at current and given branch.
     */
    private static void merge(String branchName) throws GitletException {
        if (!Branch.exists(branchName)) {
            throw mergeBranchNotExistException();
        }
        if (branchName.equals(getBranch())) {
            throw mergeCanNotMergeItselfException();
        }
        String currentBranch = getBranch();
        Commit currentCommit = getHead();
        Commit mergedCommit = Branch.get(branchName);
        Commit splitPoint = Commit.splitPoint(currentCommit, mergedCommit);
        untrackedCheck(mergedCommit, mergeDangerousException());
        if (splitPoint.equals(mergedCommit)) {
            throw mergeGivenBranchIsAncestorMessage();
        }
        if (splitPoint.equals(currentCommit)) {
            checkout(branchName);
            throw mergeCurrentBranchIsAncestorMessage();
        }
        for (String fileName : splitPoint.files()) {
            if (currentCommit.contains(fileName) && mergedCommit.contains(fileName)
                    && Commit.different(splitPoint, mergedCommit, fileName)
                    && !Commit.different(splitPoint, currentCommit, fileName)) {
                Repository.checkout(mergedCommit, fileName);
                stagingAdd(fileName);
            }
        }
        for (String fileName : mergedCommit.files()) {
            if (!splitPoint.contains(fileName) && !currentCommit.contains(fileName)) {
                Repository.checkout(mergedCommit, fileName);
                stagingAdd(fileName);
            }
        }
        for (String fileName : splitPoint.files()) {
            if (currentCommit.contains(fileName)
                    && !Commit.different(splitPoint, currentCommit, fileName)
                    && !mergedCommit.contains(fileName)) {
                rm(fileName);
            }
        }
        Set<String> potentialConflictFiles = new HashSet<>(mergedCommit.files());
        potentialConflictFiles.addAll(currentCommit.files());
        boolean conflictExists = false;
        for (String fileName : potentialConflictFiles) {
            boolean conflict = false;
            boolean currentExists = currentCommit.contains(fileName);
            boolean mergedExists = mergedCommit.contains(fileName);
            if (splitPoint.contains(fileName)) {
                if (currentExists && mergedExists
                        && Commit.different(splitPoint, currentCommit, fileName)
                        && Commit.different(splitPoint, mergedCommit, fileName)
                        && Commit.different(currentCommit, mergedCommit, fileName)) {
                    conflict = true;
                }
                if (!(currentExists && mergedExists)
                        && (
                        (currentExists && Commit.different(splitPoint, currentCommit, fileName))
                        || (mergedExists && Commit.different(splitPoint, mergedCommit, fileName)))
                ) {
                    conflict = true;
                }
            } else {
                if (currentExists && mergedExists
                        && Commit.different(currentCommit, mergedCommit, fileName)) {
                    conflict = true;
                }
            }
            if (conflict) {
                conflictExists = true;
                mergeFile(currentCommit, mergedCommit, fileName);
                stagingAdd(fileName);
            }
        }
        String commitMessage = mergeMessage(currentBranch, branchName);
        mergedCommit(branchName, commitMessage);
        if (conflictExists) {
            throw mergeHasConflictMessage();
        }
    }

    private static void mergeFile(Commit currentCommit, Commit mergedCommit, String fileName) {
        if (!fileOf(fileName).exists()) {
            createFile(fileOf(fileName));
        }
        String currentContent = currentCommit.contains(fileName)
                ? readContentsAsString(currentCommit.getFile(fileName)) : "";
        String mergedContent = mergedCommit.contains(fileName)
                ? readContentsAsString(mergedCommit.getFile(fileName)) : "";
        writeContents(fileOf(fileName), "<<<<<<< HEAD\n",
                currentContent, "=======\n", mergedContent, ">>>>>>>\n");
    }

    private static String mergeMessage(String currentBranch, String givenBranch) {
        return "Merged " + givenBranch + " into " + currentBranch + ".";
    }

    private static void addRemote(String[] args) throws GitletException {
        addRemote(args[0], args[1]);
    }

    private static void addRemote(String remoteName, String path) throws GitletException {
        if (Remote.exists(remoteName)) {
            throw remoteAlreadyExistsException();
        }
        Remote.remoteAdd(remoteName, path);
    }

    private static void rmRemote(String[] args) throws GitletException {
        rmRemote(args[0]);
    }

    private static void rmRemote(String remoteName) throws GitletException {
        if (!Remote.exists(remoteName)) {
            throw remoteNameNotExistException();
        }
        Remote.remoteRemove(remoteName);
    }

    private static void push(String[] args) throws GitletException {
        push(args[0], args[1]);
    }

    private static void push(String remoteName, String remoteBranchName) throws GitletException {
        if (!Remote.exists(remoteName)) {
            throw remoteNameNotExistException();
        }
        if (!Remote.remoteRepo(remoteName).exists()) {
            throw remoteNotFoundException();
        }

        Commit remoteBranchHead;
        if (!Remote.hasBranch(remoteName, remoteBranchName)) {
            remoteBranchHead = Commit.getInitialCommit();
        } else {
            remoteBranchHead = Remote.getBranch(remoteName, remoteBranchName);
        }

        HashSet<String> ancestors = Commit.ancestorsOf(getHead());
        if (!ancestors.contains(remoteBranchHead.id)) {
            throw remoteNeedPullDownFirstException();
        }
        ancestors.removeAll(Commit.ancestorsOf(remoteBranchHead));
        for (String commitId : ancestors) {
            Remote.copyCommit(GITLET_DIR, Remote.remoteRepo(remoteName), commitId);
        }

        Remote.setBranch(remoteName, remoteBranchName, getHead().id);
    }

    private static void fetch(String[] args) throws GitletException {
        fetch(args[0], args[1]);
    }

    private static void fetch(String remoteName, String remoteBranchName) throws GitletException {
        if (!Remote.exists(remoteName)) {
            throw remoteNameNotExistException();
        }
        if (!Remote.remoteRepo(remoteName).exists()) {
            throw remoteNotFoundException();
        }
        if (!Remote.hasBranch(remoteName, remoteBranchName)) {
            throw remoteBranchNotExistException();
        }

        Commit remoteBranchHead = Remote.getBranch(remoteName, remoteBranchName);
        HashSet<String> ancestors = Commit.ancestorsOf(
                Remote.remoteCommitDir(remoteName), remoteBranchHead);
        for (String commitId : ancestors) {
            Remote.copyCommit(Remote.remoteRepo(remoteName), GITLET_DIR, commitId);
        }

        Branch.set(fetchBranchName(remoteName, remoteBranchName), remoteBranchHead.id);
    }

    private static String fetchBranchName(String remoteName, String remoteBranchName) {
        return remoteName + "/" + remoteBranchName;
    }

    private static void pull(String[] args) throws GitletException {
        pull(args[0], args[1]);
    }

    private static void pull(String remoteName, String remoteBranchName) throws GitletException {
        fetch(remoteName, remoteBranchName);
        String branchName = fetchBranchName(remoteName, remoteBranchName);
        merge(branchName);
    }
}
