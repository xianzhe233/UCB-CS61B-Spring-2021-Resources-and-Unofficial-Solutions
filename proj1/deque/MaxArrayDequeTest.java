package deque;
import org.junit.Test;
import static org.junit.Assert.*;
import java.util.Comparator;
import java.util.LinkedList;

public class MaxArrayDequeTest {

    private class lenComparator implements Comparator<String> {
        public int compare(String o1, String o2) {
            return o1.length() - o2.length();
        }
    }

    @Test
    /** Create a string MaxArrayDeque and tests max() method. Since max() uses the parameter version
     * max(), there's no need to test individually. */
    public void maxTest() {
        Comparator<String> c = new lenComparator();
        MaxArrayDeque<String> d = new MaxArrayDeque<>(c);
        d.addLast("a");
        d.addLast("word");
        d.addLast("theLongest");
        d.addLast("xianzhe");

        assertEquals("theLongest", d.max());
    }
}