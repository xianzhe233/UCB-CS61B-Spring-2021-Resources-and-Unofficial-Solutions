package deque;

import java.util.Iterator;

public class ArrayDeque<T> implements Deque<T>, Iterable<T> {
    private T[] array;
    private int size;
    private int nextFirst;
    private int nextLast;

    private static final int INIT_CAPACITY = 8;
    private static final int RESIZE_THRESHOLD = INIT_CAPACITY * 2;
    private static final double USAGE_FACTOR = 0.25;

    public ArrayDeque() {
        array = (T[]) new Object[INIT_CAPACITY];
        size = 0;
        nextFirst = 0;
        nextLast = 1;
    }

    /**
     * Maps index into proper circular position to keep array circular.
     */
    private int circulate(int index) {
        return (index + array.length) % array.length;
    }

    /**
     * Returns the first item's position in array.
     */
    private int firstPos() {
        return circulate(nextFirst + 1);
    }

    /**
     * Returns the last item's position in array.
     */
    private int lastPos() {
        return circulate(nextLast - 1);
    }

    /**
     * Resizes array to length newSize.
     */
    private void resize(int newSize) {
        T[] newArray = (T[]) new Object[newSize];
        int begin = firstPos();
        int end = lastPos();
        int newBegin = newSize / 4;
        int newEnd = newBegin + size - 1;

        if (begin < end) {
            System.arraycopy(array, begin, newArray, newBegin, size);
        } else {
            System.arraycopy(array, begin, newArray, newBegin, size - begin);
            System.arraycopy(array, 0, newArray, newBegin + size - begin, end + 1);
        }

        nextFirst = newBegin - 1;
        nextLast = newEnd + 1;
        array = newArray;
    }

    /**
     * If array is full before adding an item, resizes it to double.
     */
    private void resizeDouble() {
        if (size == array.length) {
            resize(array.length * 2);
        }
    }

    /**
     * If array's usage factor < 25% and array's length >= 16 after deleting an item,
     * resizes it to half.
     */
    private void resizeHalf() {
        double usageFactor = (double) size / (double) array.length;
        if (usageFactor < USAGE_FACTOR && array.length >= RESIZE_THRESHOLD) {
            resize(array.length / 2);
        }
    }

    /**
     * Adds an item of type T to the front of the deque.
     */
    @Override
    public void addFirst(T item) {
        resizeDouble();

        size++;
        array[nextFirst] = item;
        nextFirst = circulate(nextFirst - 1);
    }

    /**
     * Adds an item of type T to the back of the deque.
     */
    @Override
    public void addLast(T item) {
        resizeDouble();

        size++;
        array[nextLast] = item;
        nextLast = circulate(nextLast + 1);
    }

    /**
     * Returns the number of items in the deque.
     */
    @Override
    public int size() {
        return size;
    }

    /**
     * Prints the items in the deque from first to last, separated by a space.
     * Once all the items have been printed, print out a new line.
     */
    @Override
    public void printDeque() {
        for (int i = firstPos(); true; i = circulate(i + 1)) {
            System.out.print(array[i] + " ");
            if (i == lastPos()) {
                break;
            }
        }
        System.out.println();
    }

    /**
     * Removes and returns the item at the front of the deque. If no such item exists, returns null.
     */
    @Override
    public T removeFirst() {
        if (isEmpty()) {
            return null;
        }

        int pos = firstPos();
        T item = array[pos];
        array[pos] = null;
        size--;
        nextFirst = pos;
        resizeHalf();

        return item;
    }

    /**
     * Removes and returns the item at the back of the deque. If no such item exists, returns null.
     */
    @Override
    public T removeLast() {
        if (isEmpty()) {
            return null;
        }

        int pos = lastPos();
        T item = array[pos];
        array[pos] = null;
        size--;
        nextLast = pos;
        resizeHalf();

        return item;
    }

    /**
     * Gets the item at the given index, where 0 is the front, 1 is the next item, and so forth.
     * If no such item exists, returns null.
     */
    @Override
    public T get(int index) {
        if (index >= size) {
            return null;
        } else {
            return array[circulate(firstPos() + index)];
        }
    }

    private class ArrayDequeIterator implements Iterator<T> {
        int p = 0;

        public boolean hasNext() {
            return p < size;
        }

        public T next() {
            T returnItem = get(p);
            p += 1;
            return returnItem;
        }
    }

    public Iterator<T> iterator() {
        return new ArrayDequeIterator();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || !(o instanceof Deque) || !(o instanceof Iterable)) {
            return false;
        }

        Deque<T> d = (Deque<T>) o;
        Iterator<T> it1 = this.iterator();
        Iterator<?> it2 = ((Iterable<?>) o).iterator();

        if (this.size != d.size()) {
            return false;
        }

        while (it1.hasNext()) {
            T item1 = it1.next();
            Object item2 = it2.next();
            if (!item1.equals(item2)) {
                return false;
            }
        }

        return true;
    }

}
