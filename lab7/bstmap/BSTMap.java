package bstmap;

import java.security.Key;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class BSTMap<K extends Comparable, V> implements Map61B<K, V>{
    /** Node class includes key and value.*/
    private class Node<K, V> {
        K key;
        V value;
        Node<K, V> left, right;
        public Node(K key, V value, Node<K, V> left, Node<K, V> right) {
            this.key = key;
            this.value = value;
            this.left = left;
            this.right = right;
        }

        /** Prints subtrees' values recursively. */
        public void print() {
            if (this.left != null) {
                this.left.print();
            }
            System.out.print(this.value.toString() + ' ');
            if (this.right != null) {
                this.right.print();
            }
        }
    }

    private Node root;
    private int size;
    private Set<K> keySet;

    public BSTMap() {
        this.clear();
    }

    public void clear() {
        root = null;
        size = 0;
        keySet = new HashSet<K>();
    }

    public boolean containsKey(K key) {
        return keySet.contains(key);
    }

    /** Finds key's value from tree. */
    private V find(Node<K, V> tree, K key) {
        if (tree == null) return null;
        if (tree.key.equals(key)) return tree.value;
        else {
            if (key.compareTo(tree.key) < 0) return find(tree.left, key);
            else {
                return find(tree.right, key);
            }
        }
    }

    public V get(K key) {
        return (V) find(root, key);
    }

    public int size() {
        return size;
    }

    /** Inserts a key-value pair into tree. */
    private Node<K, V> insert(Node<K, V> tree, K key, V value) {
        if (tree == null) {
            keySet.add(key);
            size++;
            return new Node(key, value, null, null);
        }
        if (key.compareTo(tree.key) < 0) {
            tree.left = insert(tree.left, key, value);
        }
        else {
            tree.right = insert(tree.right, key, value);
        }
        return tree;
    }

    public void put(K key, V value) {
        root = insert(root, key, value);
    }

    public Set<K> keySet() {
        return keySet;
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
        if (root != null) {
            root.print();
        }
        System.out.println();
    }
}
