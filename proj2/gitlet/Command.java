package gitlet;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static gitlet.GitletException.*;
import static gitlet.Repository.*;

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
        if(!isFileExist(fileName)) {
            throw addFileNotExistException();
        }

        if (!isStagedAddition(fileName)) {
            stagingAdd(fileName);
        } else {
            Commit currentCommit = getHead();
            if (currentCommit.contains(fileName) && !currentCommit.isChanged(fileOf(fileName))) {
                stagingRemove(fileName);
            } else {
                stagingAdd(fileName);
            }
        }
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

        Repository.commit(message);
    }

    private static void rm(String[] args) throws GitletException {
    }

    private static void log() throws GitletException {
    }

    private static void globalLog() throws GitletException {
    }

    private static void find(String[] args) throws GitletException {
    }

    private static void status() throws GitletException {
    }

    private static void checkout(String[] args) throws GitletException {
    }

    private static void branch(String[] args) throws GitletException {
    }

    private static void rmBranch(String[] args) throws GitletException {
    }

    private static void reset(String[] args) throws GitletException {
    }

    private static void merge(String[] args) throws GitletException {
    }
}
