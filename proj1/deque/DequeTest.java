package deque;

import edu.princeton.cs.algs4.StdRandom;
import org.junit.Test;

import static org.junit.Assert.*;


public class DequeTest {

    @Test
    /** Creates two different Deques with the same address, test equals() methods. */
    public void totallyEqualsTest() {
        ArrayDeque<Integer> ad1 = new ArrayDeque<>();
        ad1.addFirst(1);
        ad1.addFirst(2);
        ArrayDeque<Integer> ad2 = ad1;
        assertTrue(ad1.equals(ad2));

        LinkedListDeque<Integer> lld1 = new LinkedListDeque<>();
        lld1.addFirst(1);
        lld1.addFirst(2);
        LinkedListDeque<Integer> lld2 = lld1;
        assertTrue(lld1.equals(lld2));
    }

    @Test
    /** Creates two different Deques with same subclass,
     * test equals() behavior within same subclass. */
    public void sameSubclassEqualsTest() {
        ArrayDeque<Integer> ad1 = new ArrayDeque<>();
        ad1.addFirst(1);
        ArrayDeque<Integer> ad2 = new ArrayDeque<>();
        ad2.addFirst(1);
        assertTrue(ad1.equals(ad2));

        ad1.addFirst(2);
        ad2.addFirst(3);
        assertFalse(ad1.equals(ad2));

        LinkedListDeque<Integer> lld1 = new LinkedListDeque<>();
        lld1.addFirst(1);
        LinkedListDeque<Integer> lld2 = new LinkedListDeque<>();
        lld2.addFirst(1);
        assertTrue(lld1.equals(lld2));

        lld1.addFirst(2);
        lld2.addFirst(3);
        assertFalse(lld1.equals(lld2));
    }

    @Test
    /** Creates two Deques with different subclasses and same items, tests equals().
     *  Then makes them different, test equals() again. */
    public void transSubclassEqualsTest() {
        LinkedListDeque<Integer> d1 = new LinkedListDeque<>();
        ArrayDeque<Integer> d2 = new ArrayDeque<>();
        d1.addFirst(1);
        d2.addFirst(1);
        assertTrue(d1.equals(d2));

        d1.addFirst(2);
        d2.addFirst(3);
        assertFalse(d1.equals(d2));
    }

    @Test
    public void randomizedEqualsTest() {
        LinkedListDeque<Integer> d1 = new LinkedListDeque<>();
        LinkedListDeque<Integer> d2 = new LinkedListDeque<>();
        ArrayDeque<Integer> d3 = new ArrayDeque<>();
        ArrayDeque<Integer> d4 = new ArrayDeque<>();
        int N = 50000;
        for (int i = 0; i < N; i += 1) {
            int operationNumber = StdRandom.uniform(0, 2);
            boolean operationPos = StdRandom.bernoulli();
            if (operationNumber == 0) {
                addAll(d1, d2, d3, d4, operationPos);
            } else {
                removeAll(d1, d2, d3, d4, operationPos);
            }
            assertTrue(d1.equals(d2) && d3.equals(d4) && d1.equals(d3));
        }
    }

    public void addAll(Deque<Integer> d1, Deque<Integer> d2,
                       Deque<Integer> d3, Deque<Integer> d4, boolean isAddFirst) {
        int item = StdRandom.uniform(0, 10000);
        if (d1.isEmpty()) {
            return;
        }
        if (isAddFirst) {
            d1.addFirst(item);
            d2.addFirst(item);
            d3.addFirst(item);
            d4.addFirst(item);
        } else {
            d1.addLast(item);
            d2.addLast(item);
            d3.addLast(item);
            d4.addLast(item);
        }
    }

    public void removeAll(Deque<Integer> d1, Deque<Integer> d2,
                          Deque<Integer> d3, Deque<Integer> d4, boolean isRemoveFirst) {
        if (isRemoveFirst) {
            d1.removeFirst();
            d2.removeFirst();
            d3.removeFirst();
            d4.removeFirst();
        } else {
            d1.removeLast();
            d2.removeLast();
            d3.removeLast();
            d4.removeLast();
        }
    }
}
