package deque;
import java.util.Iterator;

public interface Deque<T> {

    void addFirst(T item);
    void addLast(T item);
    boolean isEmpty();
    int size();
    void printDeque();
    T removeFirst();
    T removeLast();
    T get(int index);
    Iterator<T> iterator();

    /** Returns whether or not the parameter o is equal to the Deque.
     *  o is considered equal if it is a Deque and if it contains th
     *  e same contents (as goverened by the generic T’s equals method) in the same order.
     *
     *  I take the lecture videos that after 2021, which used instanceof key word instead of
     *  classOf() method. So I comment the newer version of equals() and rewrite the old version
     *  so that I can use gradescope to compile my code.*/
    boolean equals(Object o);
}
