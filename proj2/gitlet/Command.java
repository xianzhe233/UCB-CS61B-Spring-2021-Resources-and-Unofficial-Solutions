package gitlet;

import java.io.File;
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
            Map.entry("merge", Set.of(1))
    );

    static void process(String[] args) throws GitletException {
        if (args.length == 0) {
            throw NoArgsException();
        }

        String command = args[0];
        String[] commandArgs = Arrays.copyOfRange(args, 1, args.length);

        if (!ARGS_MAP.containsKey(command)) {
            throw CommandNotExistException();
        }
        if (!ARGS_MAP.get(command).contains(commandArgs.length)) {
            throw OperandsIncorrectException();
        }

        if (command.equals("init")) {
            init();
        } else {
            if (!exists()) {
                throw UninitializedException();
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
        List<String> WDF = workingDirectoryFiles();
        List<String> modifiedFiles = modifiedFiles(WDF);
        List<String> untrackedFiles = untrackedFiles(WDF);
        HashSet<String> reunstagedFiles = new HashSet<>(modifiedFiles);
        reunstagedFiles.addAll(untrackedFiles);
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
        for (String file : stagedFiles) {
            if (reunstagedFiles.contains(file)) {
                continue;
            }
            System.out.println(file);
        }
        System.out.println();

        // removed files
        System.out.println("=== Removed Files ===");
        ArrayList<String> removedFiles = new ArrayList<>(getRemoval());
        Collections.sort(removedFiles);
        for (String file : removedFiles) {
            if (reunstagedFiles.contains(file)) {
                continue;
            }
            System.out.println(file);
        }
        System.out.println();


        // modifications not staged for commit
        System.out.println("=== Modifications Not Staged For Commit ===");
        Collections.sort(modifiedFiles);
        for (String file : modifiedFiles) {
            String afterName;
            if (!fileOf(file).exists()) {
                afterName = " (deleted)";
            } else {
                afterName = " (modified)";
            }
            System.out.println(file + afterName);
        }
        System.out.println();

        // untracked files
        System.out.println("=== Untracked Files ===");
        Collections.sort(untrackedFiles);
        for (String file : untrackedFiles) {
            System.out.println(file);
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
        }
    }

    /**
     * Copies f1's content to f2.
     * former -> latter
     */
    private static void copy(File f1, File f2) {
        writeContents(f2, readContentsAsString(f1));
    }

    private static void checkout(String dash, String fileName) throws GitletException {
        if (!dash.equals("--")) {
            throw OperandsIncorrectException();
        }
        if (!getHead().contains(fileName)) {
            throw checkoutFileNotExistException();
        }

        File fileOfHead = getHead().getFile(fileName);
        copy(fileOfHead, fileOf(fileName));
    }

    private static void checkout(String commitId, String dash, String fileName) throws GitletException {
        if (!dash.equals("--")) {
            throw OperandsIncorrectException();
        }
        if (!Commit.exists(commitId)) {
            throw checkoutCommitNotExistException();
        }
        Commit commit = Commit.get(commitId);
        if (!commit.contains(fileName)) {
            throw checkoutFileNotExistException();
        }

        File fileOfCommit = commit.getFile(fileName);
        copy(fileOfCommit, fileOf(fileName));
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
        setHead(branchName);
        List<String> untrackedFiles = untrackedFiles(workingDirectoryFiles());

        for (String fileName : untrackedFiles) {
            if (branchCommit.contains(fileName)) {
                throw checkoutDangerousException();
            }
        }

        for (String fileName : currentCommit.files()) {
            if (!branchCommit.contains(fileName)) {
                fileOf(fileName).delete();
            }
        }

        for(String fileName : branchCommit.files()) {
            if (!fileOf(fileName).exists()) {
                createFile(fileOf(fileName));
            }
            copy(branchCommit.getFile(fileName), fileOf(fileName));
        }

        clearStagingArea();
    }

    private static void branch(String[] args) throws GitletException {
        branch(args[0]);
    }

    private static void branch(String branchName) throws GitletException {
        if (!Branch.exists(branchName)) {
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
    }

    private static void merge(String[] args) throws GitletException {
    }
}
