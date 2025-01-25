package gitlet;

import java.io.File;
import static gitlet.Utils.*;


/** Represents a gitlet repository.
 * Repository class maintains staging-area and methods to access working-directory,
 * manipulates blobs, commits, head, branches to provide general ways to work
 * with the repository.
 *
 *  @author xianzhe233
 */
public class Repository {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */

    /** The current working directory. */
//    public static final File CWD = new File(System.getProperty("user.dir"));
// TODO: Change CWD back finally.
    public static final File CWD = new File("playground");
    /** The .gitlet directory. */
    public static final File GITLET_DIR = join(CWD, ".gitlet");

    /* TODO: fill in the rest of this class. */
}
