package gitlet;
import org.junit.Test;
import static org.junit.Assert.*;
import static gitlet.Utils.*;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class CommitTest {
    static final String INITIAL_ID = "2bca61509088a86ded75abaf7eb7ff15f331fad6";

    @Test
    public void timeStampTest() {
        long timeStamp = Commit.getTimeStamp();
        System.out.println(timeStamp);
        String date = Commit.dateOf(timeStamp);
        System.out.println(date);
        String initDate = Commit.dateOf(0);
        System.out.println(initDate);
    }

    @Test
    public void initialCommitTest() {
        Commit init = Commit.getInitialCommit();
        System.out.println(init);
        System.out.println(Commit.dateOf(0));
    }

    @Test
    public void commitExistsTest() {
        Commit.getInitialCommit();
        assertTrue(Commit.exists(INITIAL_ID));
    }

    @Test
    public void commitGetTest() {
        Commit init = Commit.getInitialCommit();
        Commit initCommit = Commit.get(INITIAL_ID);
        assertEquals(init.toString(), initCommit.toString());
        System.out.println(initCommit);
    }

    @Test
    public void commitGetAbbreviationTest() {
        Commit init = Commit.getInitialCommit();
        Commit initCommit = Commit.get(INITIAL_ID.substring(0, 6));
        assertEquals(init.toString(), initCommit.toString());
        System.out.println(initCommit);
    }

    @Test
    public void commitPrintTest() {
        Commit init = Commit.getInitialCommit();
        Commit newCommit = new Commit(init, null, "new testing commit", new HashMap<>(), new HashSet<>());
//        newCommit.print();
//        init.print();
//        Temporary test, print() needs to be private.
    }

    @Test
    public void commitLogTest() {
        Commit init = Commit.getInitialCommit();
        Commit commit1 = new Commit(init, null, "commit1", new HashMap<>(), new HashSet<>());
        Commit commit2 = new Commit(commit1, null, "commit2", new HashMap<>(), new HashSet<>());
        Commit commit3 = new Commit(commit2, null, "commit3", new HashMap<>(), new HashSet<>());
        Commit commit4 = new Commit(commit1, null, "commit4", new HashMap<>(), new HashSet<>());
        Commit.log(commit3);
    }

//    @Test
//    public void commitGetAllTest() {
//        List<String> commits = Commit.getAllCommits();
//        System.out.println(commits);
//    }

    @Test
    public void commitGlobalLogTest() {
        Commit.globalLog();
    }

    @Test
    public void splitPointTest() {
        Commit init = Commit.getInitialCommit();
        Commit commit1 = new Commit(init, null, "commit1", new HashMap<>(), new HashSet<>());
        Commit commit2 = new Commit(commit1, null, "commit2", new HashMap<>(), new HashSet<>());
        Commit commit3 = new Commit(commit1, null, "commit3", new HashMap<>(), new HashSet<>());
        Commit commit4 = new Commit(commit3, null, "commit4", new HashMap<>(), new HashSet<>());
        Commit splitPoint = Commit.splitPoint(commit2, commit4);
        System.out.println(splitPoint);
        assertTrue(splitPoint.equals(commit1));
    }

    @Test
    public void isChangedTest() {
        Commit init = Commit.getInitialCommit();
        File testFile = new File("testing/src/commitTest.txt");
        writeContents(testFile, "Status1");
        String blobId = Blob.createBlob(testFile);
        HashMap<String, String> map = new HashMap<>();
        map.put("commitTest.txt", blobId);
        Commit commit = new Commit(init, null, "test commit", map, new HashSet<>());
        System.out.println(commit.getFile("commitTest.txt"));
        writeContents(testFile, "Status2");
        assertTrue(commit.isChanged(testFile));
    }
}
