package deque;

import org.junit.Test;
import static org.junit.Assert.*;

public class ArrayDequeTest {

    @Test
    /** Adds a few things to the ArrayDeque, test if isEmpty() and size() work correctly. */
    public void addIsEmptySizeTest() {
        ArrayDeque<Integer> deque = new ArrayDeque<>();
        assertTrue(deque.isEmpty());
        for (int i = 1; i <= 7; i++) {
            deque.addFirst(i);
            assertEquals(i, deque.size());
        }
    }

    @Test
    /** Adds a few things to the ArrayDeque, test removeFirst() and removeLast(); */
    public void addRemoveTest() {
        ArrayDeque<Integer> deque = new ArrayDeque<>();
        deque.addFirst(1);
        deque.addFirst(2);
        deque.addFirst(3);
        int a = deque.removeFirst();
        int b = deque.removeLast();
        int c = deque.removeFirst();
        assertEquals(3, a);
        assertEquals(1, b);
        assertEquals(2, c);
    }

    @Test
    /** Adds a few things to the ArrayDeque, test removeFirst() and removeLast(); */
    public void addGetTest() {
        ArrayDeque<Integer> deque = new ArrayDeque<>();
        for (int i = 0; i <= 7; i++) {
            deque.addLast(i);
            int num = deque.get(i);
            assertEquals(i, num);
        }
    }
}
