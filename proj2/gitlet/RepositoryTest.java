package gitlet;
import java.io.File;
import java.util.List;

import org.junit.Test;

import static gitlet.Repository.*;
import static org.junit.Assert.*;
import gitlet.*;
import static gitlet.Utils.*;

public class RepositoryTest {
    @Test
    public void initTest() {
        assertFalse(exists());
        initRepository();
        assertTrue(exists());
    }

    @Test
    public void headTest() {
        initRepository();
        assertEquals(getBranch(), "master");
        Commit.log(getHead());
        commit("First commit");
        Commit.log(getHead());
        Branch.set("newBranch",getHead());
        setHead("newBranch");
        assertEquals(getBranch(), "newBranch");
    }

    @Test
    public void WDFTest() {
        initRepository();
        File f1 = join(CWD, "f1");
        File f2 = join(CWD, "f2");
        File f3 = join(CWD, "f3");
        createFile(f1);
        createFile(f2);
        createFile(f3);
        List<String> WDF = workingDirectoryFiles();
        System.out.println(WDF);
        f1.delete();
        System.out.println(workingDirectoryFiles());
        f2.delete();
        f3.delete();
    }

    @Test
    public void stagingTest() {
        initRepository();
        File f1 = join(CWD, "f1");
        File f2 = join(CWD, "f2");
        createFile(f1);
        createFile(f2);
        writeContents(f1, "Only world");
        stagingAdd("f1");
        System.out.println(untrackedFiles(workingDirectoryFiles()));
        writeContents(f1, "Hello World");
        List<String> modified = modifiedFiles(workingDirectoryFiles());
        System.out.println(modified);
        stagingAdd("f2");
        System.out.println(untrackedFiles(workingDirectoryFiles()));
        removeFromAddition("f2");
        System.out.println(untrackedFiles(workingDirectoryFiles()));
        stagingRemove("f2");
        f2.delete();
        System.out.println(untrackedFiles(workingDirectoryFiles()));
    }

    @Test
    public void checkoutTest() {
        initRepository();
        File f1 = join(CWD, "f1");
        File f2 = join(CWD, "f2");
        createFile(f1);
        createFile(f2);
        writeContents(f1, "Hello World");
        writeContents(f2, "Hello World");
        stagingAdd("f1");
        stagingAdd("f2");
        commit("First commit");
        writeContents(f1, "Changed contents");
        f2.delete();
        assertEquals(readContentsAsString(f1), "Changed contents");
        assertFalse(f2.exists());
        checkout(getHead(), "f1");
        checkout(getHead(), "f2");
        assertEquals(readContentsAsString(f1), "Hello World");
        assertTrue(f2.exists());
    }
}
