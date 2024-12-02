package deque;
import java.util.Iterator;

public interface Deque<T> {

    void addFirst(T item);
    void addLast(T item);

    /** Returns true if deque is empty, false otherwise. */
    default boolean isEmpty() {
        return size() == 0;
    }

    int size();
    void printDeque();
    T removeFirst();
    T removeLast();
    T get(int index);

    /**  I take the lecture videos that after 2021, which used instanceof key word instead of
     *  classOf() method. So I comment the newer version of equals() and rewrite the old version
     *  so that I can use gradescope to compile my code.*/
}
