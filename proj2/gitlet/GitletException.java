package gitlet;

/** General exception indicating a Gitlet error.  For fatal errors, the
 *  result of .getMessage() is the error message to be printed.
 *  @author P. N. Hilfinger
 */
class GitletException extends RuntimeException {


    /** A GitletException with no message. */
    GitletException() {
        super();
    }

    /** A GitletException MSG as its message. */
    GitletException(String msg) {
        super(msg);
    }

    static GitletException NoArgsException() {
        return new GitletException("Please enter a command.");
    }

    static GitletException CommandNotExistException() {
        return new GitletException("No command with that name exists.");
    }

    static GitletException OperandsIncorrectException() {
        return new GitletException("Incorrect operands.");
    }

    static GitletException UninitializedException() {
        return new GitletException("Not in an initialized Gitlet directory.");
    }

    static GitletException initGitletAlreadyExistsException() {
        return new GitletException("A Gitlet version-control system already exists in the current directory.");
    }

    static GitletException addFileNotExistException() {
        return new GitletException("File does not exist.");
    }

    static GitletException commitNoStagedFileException() {
        return new GitletException("No changes added to the commit.");
    }

    static GitletException commitEmptyMessageException() {
        return new GitletException("Please enter a commit message.");
    }

    static GitletException rmNoReasonToRemoveException() {
        return new GitletException("No reason to remove the file.");
    }

    static GitletException findNoSameMessageException() {
        return new GitletException("Found no commit with that message.");
    }

    static GitletException checkoutCommitNotExistException() {
        return new GitletException("No commit with that id exists.");
    }

    static GitletException checkoutFileNotExistException() {
        return new GitletException("File does not exist in that commit.");
    }

    static GitletException checkoutBranchNotExistException() {
        return new GitletException("No such branch exists.");
    }

    static GitletException checkoutCurrentBranchException() {
        return new GitletException("No need to checkout the current branch.");
    }

    static GitletException checkoutDangerousException() {
        return new GitletException("There is an untracked file in the way; delete it, or add and commit it first.");
    }

    static GitletException branchAlreadyExistsException() {
        return new GitletException("A branch with that name already exists.");
    }

    static GitletException rmBranchNotExistException() {
        return new GitletException("A branch with that name does not exist.");
    }

    static GitletException rmBranchRemoveCurrentBranchException() {
        return new GitletException("Cannot remove the current branch.");
    }
}
