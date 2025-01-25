package gitlet;
import static gitlet.Utils.*;
import org.junit.Test;

import java.util.HashMap;
import java.util.HashSet;

import static org.junit.Assert.*;
import static gitlet.Branch.*;

public class BranchTest {
    @Test
    public void branchCreateTest() {
        Commit init = Commit.getInitialCommit();
        set("master", init);
    }

    @Test
    public void branchChangeTest() {
        Commit init = Commit.getInitialCommit();
        set("master", init);
        Commit commit1 = new Commit(init, null, "test commit.", new HashMap<>(), new HashSet<>());
        set("testBranch", commit1);
        set("master", commit1);
    }

    @Test
    public void branchExistsTest() {
        Commit init = Commit.getInitialCommit();
        set("master", init);
        set("testBranch", init);
        assertTrue(exists("master"));
        assertTrue(exists("testBranch"));
    }

    @Test
    public void branchRemoveTest() {
        Commit init = Commit.getInitialCommit();
        Commit commit = new Commit(init, null, "test commit.", new HashMap<>(), new HashSet<>());
        set("removeTestBranch", commit);
        assertTrue(exists("removeTestBranch"));
        remove("removeTestBranch");
        assertFalse(exists("removeTestBranch"));
    }

    @Test
    public void allBranchesTest() {
        Commit init = Commit.getInitialCommit();
        set("branch1", init);
        set("branch2", init);
        set("branch3", init);
        set("branch4", init);
        set("branch5", init);
        System.out.println(allBranches());
    }
}
