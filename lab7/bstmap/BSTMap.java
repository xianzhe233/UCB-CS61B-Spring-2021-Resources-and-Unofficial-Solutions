package bstmap;

import java.security.Key;
import java.util.Iterator;
import java.util.Set;

public class BSTMap<K extends Comparable, V> implements Map61B<K, V>{
    /** Node class includes key and value. */
    private class Node<K, V> {
        K key;
        V value;
        public Node(K key, V value) {
            this.key = key;
            this.value = value;
        }
    }

    private Node root;
    private int size;

    public BSTMap() {
        this.clear();
    }

    public void clear() {
        root = null;

    }

    public boolean containsKey(K key) {
        throw new UnsupportedOperationException();
    }

    public V get(K key) {

        throw new UnsupportedOperationException();
    }

    public int size() {
        return size;
    }

    public void put(K key, V value) {
        throw new UnsupportedOperationException();
    }

    public Set<K> keySet() {
        throw new UnsupportedOperationException();
    }

    public V remove(K key) {
        throw new UnsupportedOperationException();
    }

    public V remove(K key, V value) {
        throw new UnsupportedOperationException();
    }

    public Iterator<K> iterator() {
        throw new UnsupportedOperationException();
    }

    /** Prints out the BSTMap in order of increasing key. */
    public void printInOrder() {

    }
}
