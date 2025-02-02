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
}
