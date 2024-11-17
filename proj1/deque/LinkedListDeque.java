package deque;

public class LinkedListDeque<T> {
    Node<T> head;
    int size;

    /** Node class of the linked list. */
    private class Node<T> {
        T item;
        Node<T> next;
        Node<T> prev;

        public Node(T item, Node<T> next, Node<T> prev) {
            this.item = item;
            this.next = next;
            this.prev = prev;
        }
    }

    /** Generate a new sentinel node. */
    private Node<T> newSentinel() {
        Node<T> sentinel = new Node<>(null, null, null);
        sentinel.next = sentinel;
        sentinel.prev = sentinel;
        return sentinel;
    }

    public LinkedListDeque() {
        head = newSentinel();
        size = 0;
    }

    /** Adds an item of type T to the front of the deque.*/
    public void addFirst(T item) {
        size++;

        Node<T> newNode = new Node<>(item, head.next, head);
        if (this.isEmpty()) {
            head.prev = newNode;
        }
        head.next.prev = newNode;
        head.next = newNode;
    }

    /** Adds an item of type T to the back of the deque. */
    public void addLast(T item) {
        size++;

        Node<T> newNode = new Node<>(item, head, head.prev);
        if (this.isEmpty()) {
            head.next = newNode;
        }
        head.prev.next = newNode;
        head.prev = newNode;
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
        Node<T> p = head.next;
        while (p != head) {
            System.out.print(p.item + " ");
            p = p.next;
        }
        System.out.println("");
    }

    /** Removes and returns the item at the front of the deque. If no such item exists, returns null. */
    public T removeFirst() {
        if (isEmpty()) {
            return null;
        }

        Node<T> p = head.next;
        p.next.prev = head;
        head.next = p.next;
        size--;

        T item = p.item;
        p = null;
        return item;
    }

    /** Removes and returns the item at the back of the deque. If no such item exists, returns null. */
    public T removeLast() {
        if (isEmpty()) {
            return null;
        }

        Node<T> p = head.prev;
        p.prev.next = head;
        head.prev = p.prev;
        size--;

        T item = p.item;
        p = null;
        return item;
    }

    /** Gets the item at the given index, where 0 is the front, 1 is the next item, and so forth.
     * If no such item exists, returns null. */
    public T get(int index) {
        Node<T> p = head.next;
        while (index > 0) {
            p = p.next;
            if (p == head) {
                return null;
            }
            index--;
        }

        return p.item;
    }

    /** Recursive version of get(). */
    public T getRecursive(int index) {
        return recursiveGet(index, head.next);
    }

    /** Helper method of getRecursive(). */
    private T recursiveGet(int index, Node<T> p) {
        if (p == head) {
            return null;
        }
        if (index == 0) {
            return p.item;
        }
        return recursiveGet(index - 1, p.next);
    }
}

