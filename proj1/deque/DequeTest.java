package deque;
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
        assertEquals(true, ad1.equals(ad2));

        LinkedListDeque<Integer> lld1 = new LinkedListDeque<>();
        lld1.addFirst(1);
        lld1.addFirst(2);
        LinkedListDeque<Integer> lld2 = lld1;
        assertEquals(true, lld1.equals(lld2));
    }

    @Test
    /** Creates two different Deques with same subclass, test equals() behavior within same subclass. */
    public void sameSubclassEqualsTest() {
        ArrayDeque<Integer> ad1 = new ArrayDeque<>();
        ad1.addFirst(1);
        ArrayDeque<Integer> ad2 = new ArrayDeque<>();
        ad2.addFirst(1);
        assertEquals(true, ad1.equals(ad2));

        ad1.addFirst(2);
        ad2.addFirst(3);
        assertEquals(false, ad1.equals(ad2));

        LinkedListDeque<Integer> lld1 = new LinkedListDeque<>();
        lld1.addFirst(1);
        LinkedListDeque<Integer> lld2 = new LinkedListDeque<>();
        lld2.addFirst(1);
        assertEquals(true, lld1.equals(lld2));

        lld1.addFirst(2);
        lld2.addFirst(3);
        assertEquals(false, lld1.equals(lld2));
    }

    @Test
    /** Creates two Deques with different subclasses and same items, tests equals().
     *  Then makes them different, test equals() again. */
    public void transSubclassEqualsTest() {
        LinkedListDeque<Integer> d1 = new LinkedListDeque<>();
        ArrayDeque<Integer> d2 = new ArrayDeque<>();
        d1.addFirst(1);
        d2.addFirst(1);
        assertEquals(true, d1.equals(d2));

        d1.addFirst(2);
        d2.addFirst(3);
        assertEquals(false, d1.equals(d2));
    }
}
