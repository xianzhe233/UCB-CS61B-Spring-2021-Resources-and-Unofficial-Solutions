package gitlet;
import org.junit.Test;
import static org.junit.Assert.*;
import gitlet.Utils;
import gitlet.Commit;

public class CommitTest {
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
}
