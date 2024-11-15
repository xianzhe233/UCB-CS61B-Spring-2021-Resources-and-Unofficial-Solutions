package randomizedtest;

import edu.princeton.cs.algs4.StdRandom;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Created by hug.
 */
public class TestBuggyAList {
    // YOUR TESTS HERE

    /** Add num to both AListNoResizing a and BuggyAList b. */
    public void addBoth(AListNoResizing<Integer> a, BuggyAList<Integer> b, int num) {
        a.addLast(num);
        b.addLast(num);
    }

    /** Delete last of both AListNoResizing a and BuggyAList b. */
    public void deleteLastBoth(AListNoResizing<Integer> a, BuggyAList<Integer> b) {
        int itema = a.removeLast();
        int itemb = b.removeLast();
        assertEquals(itema, itemb);
    }

    @Test
    public void testThreeAddThreeRemove() {
        AListNoResizing<Integer> listA = new AListNoResizing<>();
        BuggyAList<Integer> listB = new BuggyAList<>();
        addBoth(listA, listB, 1);
        addBoth(listA, listB, 2);
        addBoth(listA, listB, 3);
        addBoth(listA, listB, 4);
        addBoth(listA, listB, 5);
        addBoth(listA, listB, 6);
        for (int i = 1; i <= 3; i++) {
            deleteLastBoth(listA, listB);
        }
    }

    @Test
    public void randomizedTest() {
        AListNoResizing<Integer> L1 = new AListNoResizing<>();
        BuggyAList<Integer> L2 = new BuggyAList<>();

        int N = 5000;
        for (int i = 0; i < N; i += 1) {
            int operationNumber = StdRandom.uniform(0, 4);
            if (operationNumber == 0) {
                // addLast
                int randVal = StdRandom.uniform(0, 100);
                L1.addLast(randVal);
                L2.addLast(randVal);
            } else if (operationNumber == 1) {
                // size
                int size1 = L1.size();
                int size2 = L2.size();
                assertEquals(size1, size2);
            } else if (operationNumber == 2) {
                // getLast
                int size1 = L1.size();
                int size2 = L2.size();
                if (size1 == 0 || size2 == 0) {
                    continue;
                }
                int lastVal1 = L1.getLast();
                int lastVal2 = L2.getLast();
                assertEquals(lastVal1, lastVal2);
            } else if (operationNumber == 3) {
                // removeLast
                int size1 = L1.size();
                int size2 = L2.size();
                if (size1 == 0 || size2 == 0) {
                    continue;
                }
                int lastVal1 = L1.removeLast();
                int lastVal2 = L2.removeLast();
                assertEquals(lastVal1, lastVal2);
            }
        }
    }
}
