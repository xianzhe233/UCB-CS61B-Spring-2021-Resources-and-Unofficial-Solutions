package bstmap;

import java.security.Key;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class BSTMap<K extends Comparable<K>, V> implements Map61B<K, V> {
    /** Node class includes key and value.*/
    private class Node<K, V> {
        K key;
        V value;
        Node<K, V> left, right, parent;
        public Node(K key, V value, Node<K, V> left, Node<K, V> right, Node<K, V> parent) {
            this.key = key;
            this.value = value;
            this.left = left;
            this.right = right;
            this.parent = parent;
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

    /** Finds key's node from tree. */
    private Node<K, V> find(Node<K, V> tree, K key) {
        if (tree == null) {
            return null;
        }
        if (tree.key.equals(key)) {
            return tree;
        } else {
            if (key.compareTo(tree.key) < 0) {
                return find(tree.left, key);
            } else {
                return find(tree.right, key);
            }
        }
    }

    public V get(K key) {
        Node node = find(root, key);
        if (node == null) {
            return null;
        }
        return (V) node.value;
    }

    public int size() {
        return size;
    }

    /** Inserts a key-value pair into tree. */
    private Node<K, V> insert(Node<K, V> tree, Node<K, V> parent, K key, V value) {
        if (tree == null) {
            keySet.add(key);
            size++;
            return new Node(key, value, null, null, parent);
        }
        if (key.compareTo(tree.key) < 0) {
            tree.left = insert(tree.left, tree, key, value);
        } else {
            tree.right = insert(tree.right, tree, key, value);
        }
        return tree;
    }

    public void put(K key, V value) {
        root = insert(root, null, key, value);
    }

    public Set<K> keySet() {
        return keySet;
    }

    /** Returns which side node is, 0 for left and 1 for right. */
    private boolean getSide(Node<K, V> node) {
        if (node.parent == null) {
            return true;
        }
        if (node.parent.left == node) {
            return false;
        } else {
            return true;
        }
    }

    /** Operation of deleting node. */
    private void delete(Node<K, V> node) {
        int children = numOfChildren(node);
        boolean side = getSide(node);
        keySet.remove(node.key);
        switch (children) {
            case 0:
                size--;
                if (node.parent == null) {
                    root = null;
                    break;
                }
                if (side == false) {
                    node.parent.left = null;
                } else {
                    node.parent.right = null;
                }
                break;
            case 1:
                size--;
                Node<K, V> child;
                if (node.left != null) {
                    child = node.left;
                } else {
                    child = node.right;
                }

                child.parent = node.parent;
                if (node.parent == null) {
                    root = child;
                } else {
                    if (side == false) {
                        node.parent.left = child;
                    } else {
                        node.parent.right = child;
                    }
                }
                break;
            case 2:
                Node<K, V> successor = successor(node);
                node.key = successor.key;
                node.value = successor.value;
                delete(successor);
                keySet.add(node.key);
                break;
        }
    }

    /** deletes node with key in tree. */
    private V delete(Node<K, V> tree, K key) {
        if (key.equals(tree.key)) {
            V value = tree.value;
            delete(tree);
            return value;
        } else {
            if (key.compareTo(tree.key) < 0) {
                return delete(tree.left, key);
            } else {
                return delete(tree.right, key);
            }
        }
    }

    public V remove(K key) {
        // If node to be deleted is leaf, just delete;
        // If the node has only one child, let the child replace it.
        // If the node has two children, let its successor replace it
        // and delete the successor.
        if (!keySet.contains(key)) {
            return null;
        }
        return (V) delete(root, key);
    }

    public V remove(K key, V value) {
        Node<K, V> node = find(root, key);
        if (value.equals(node.value)) {
            return delete(node, key);
        }
        return null;
    }

    public Iterator<K> iterator() {
        return keySet.iterator();
    }

    /** Prints out the BSTMap in order of increasing key. */
    public void printInOrder() {
        if (root != null) {
            root.print();
        }
        System.out.println();
    }

    /** Returns the successor of node. */
    private Node<K, V> successor(Node<K, V> node) {
        node = node.right;
        while (node.left != null) {
            node = node.left;
        }
        return node;
    }

    /** Returns the number of node's children. (Can be 0, 1 or 2) */
    private int numOfChildren(Node<K, V> node) {
        int cnt = 0;
        if (node.left != null) {
            cnt += 1;
        }
        if (node.right != null) {
            cnt += 1;
        }
        return cnt;
    }
}
