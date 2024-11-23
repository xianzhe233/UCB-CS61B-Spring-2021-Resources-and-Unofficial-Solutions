package deque;

public class ArrayDeque<T> {
    private T[] array;
    private int size;
    private int nextFirst;
    private int nextLast;

    public ArrayDeque() {
        array = (T[]) new Object[10];
        size = 0;
        nextFirst = 0;
        nextLast = 1;
    }

    /** Maps index into proper circular position to keep array circular. */
    private int circulate(int index) {
        return (index + array.length) % array.length;
    }

    /** Returns the first item's position in array. */
    private int firstPos() {
        return circulate(nextFirst + 1);
    }

    /** Returns the last item's position in array. */
    private int lastPos() {
        return circulate(nextLast - 1);
    }

    /** Adds an item of type T to the front of the deque.*/
    public void addFirst(T item) {
        size++;
        array[nextFirst] = item;
        nextFirst = circulate(nextFirst - 1);
    }

    /** Adds an item of type T to the back of the deque. */
    public void addLast(T item) {
        size++;
        array[nextLast] = item;
        nextLast = circulate(nextLast + 1);
    }

    /** Returns true if deque is empty, false otherwise. */
    public boolean isEmpty() {
        return size == 0;
    }

    /** Returns the number of items in the deque. */
    public int size() {
        return size;
    }

    /** Prints the items in the deque from first to last, separated by a space.
     * Once all the items have been printed, print out a new line. */
    public void printDeque() {
        for (int i = circulate(nextFirst + 1); i != nextLast; i = circulate(i + 1)) {
            System.out.print(array[i] + " ");
        }
        System.out.println();
    }

    /** Removes and returns the item at the front of the deque. If no such item exists, returns null. */
    public T removeFirst() {
        if (isEmpty()) {
            return null;
        }

        int pos = firstPos();
        T item = array[pos];
        array[pos] = null;
        size--;
        nextFirst = pos;
        return item;
    }

    /** Removes and returns the item at the back of the deque. If no such item exists, returns null. */
    public T removeLast() {
        if (isEmpty()) {
            return null;
        }

        int pos = lastPos();
        T item = array[pos];
        array[pos] = null;
        size--;
        nextLast = pos;
        return item;
    }

    /** Gets the item at the given index, where 0 is the front, 1 is the next item, and so forth.
     * If no such item exists, returns null. */
    public T get(int index) {
        if (index >= size) {
            return null;
        }

        int p = firstPos();
        T item = array[p];
        while (index > 0) {
            p = circulate(p + 1);
            item = array[p];
            index--;
        }

        return item;
    }
}
