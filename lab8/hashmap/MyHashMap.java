package hashmap;

import java.util.*;

/**
 *  A hash table-backed Map implementation. Provides amortized constant time
 *  access to elements via get(), remove(), and put() in the best case.
 *
 *  Assumes null keys will never be inserted, and does not resize down upon remove().
 *  @author YOUR NAME HERE
 */
public class MyHashMap<K, V> implements Map61B<K, V> {

    /**
     * Protected helper class to store key/value pairs
     * The protected qualifier allows subclass access
     */
    protected class Node<K, V> {
        K key;
        V value;

        Node(K k, V v) {
            key = k;
            value = v;
        }
    }

    /* Instance Variables */
    private static final int DEFAULT_INITIAL_SIZE = 16;
    private static final double DEFAULT_LOAD_FACTOR = 0.75;
    private static final int RESIZE_FACTOR = 2;

    private Collection<Node<K, V>>[] buckets;
    private int size;
    private double maxLoad;
    private HashSet<K> keySet;


    /** Constructors */
    public MyHashMap() {
        this(DEFAULT_INITIAL_SIZE, DEFAULT_LOAD_FACTOR);
    }

    public MyHashMap(int initialSize) {
        this(initialSize, DEFAULT_LOAD_FACTOR);
    }

    /**
     * MyHashMap constructor that creates a backing array of initialSize.
     * The load factor (# items / # buckets) should always be <= loadFactor
     *
     * @param initialSize initial size of backing array
     * @param maxLoad maximum load factor
     */
    public MyHashMap(int initialSize, double maxLoad) {
        buckets = createTable(initialSize);
        this.maxLoad = maxLoad;
        this.size = 0;
        keySet = new HashSet<>();
    }

    /**
     * Returns a new node to be placed in a hash table bucket
     */
    private Node<K, V> createNode(K key, V value) {
        return new Node<K, V>(key, value);
    }

    /**
     * Returns a data structure to be a hash table bucket
     *
     * The only requirements of a hash table bucket are that we can:
     *  1. Insert items (`add` method)
     *  2. Remove items (`remove` method)
     *  3. Iterate through items (`iterator` method)
     *
     * Each of these methods is supported by java.util.Collection,
     * Most data structures in Java inherit from Collection, so we
     * can use almost any data structure as our buckets.
     *
     * Override this method to use different data structures as
     * the underlying bucket type
     *
     * BE SURE TO CALL THIS FACTORY METHOD INSTEAD OF CREATING YOUR
     * OWN BUCKET DATA STRUCTURES WITH THE NEW OPERATOR!
     */
    protected Collection<Node<K, V>> createBucket() {
        return new LinkedList<>();
    }

    /**
     * Returns a table to back our hash table. As per the comment
     * above, this table can be an array of Collection objects
     *
     * BE SURE TO CALL THIS FACTORY METHOD WHEN CREATING A TABLE SO
     * THAT ALL BUCKET TYPES ARE OF JAVA.UTIL.COLLECTION
     *
     * @param tableSize the size of the table to create
     */
    private Collection<Node<K, V>>[] createTable(int tableSize) {
        return new Collection[tableSize];
    }

    /** Hash function of MyHashMap. */
    private int MyHash(K key) {
        return Math.floorMod(key.hashCode(), buckets.length);
    }

    /** Gets node with key key from bucket.
     * If it doesn't exist, return null. */
    private Node<K, V> getFromBucket(Collection<Node<K, V>> bucket, K key) {
        if (bucket == null) return null;
        for (Node<K, V> node : bucket) {
            if (node.key.equals(key)) {
                return node;
            }
        }
        return null;
    }

    @Override
    public void clear() {
        buckets = createTable(buckets.length);
        size = 0;
        keySet.clear();
    }

    @Override
    public boolean containsKey(K key) {
        if (getFromBucket(buckets[MyHash(key)], key) != null) {
            return true;
        }
        return false;
    }

    @Override
    public V get(K key) {
        Node<K, V> node = getFromBucket(buckets[MyHash(key)], key);
        return node == null ? null : node.value;
    }

    @Override
    public int size() {
        return size;
    }

    /** Returns loadFactor for now. */
    private double loadFactor() {
        return size / buckets.length;
    }

    /** If loadFactor is above maxLoad, resize it with RESIZE_FACTOR. */
    private void resizeBuckets() {
        if (loadFactor() > maxLoad) {
            LinkedList<Node<K, V>> nodes = new LinkedList<>();
            for (K key : keySet) {
                nodes.add(getFromBucket(buckets[MyHash(key)], key));
            }
            buckets = createTable(buckets.length * RESIZE_FACTOR);
            for (Node<K, V> node : nodes) {
                Collection<Node<K, V>> bucket = buckets[MyHash(node.key)];
                if (bucket == null) {
                    bucket = buckets[MyHash(node.key)] = createBucket();
                }
                buckets[MyHash(node.key)].add(node);
            }
        }
    }

    @Override
    public void put(K key, V value) {
        resizeBuckets();
        Collection<Node<K, V>> bucket = buckets[MyHash(key)];
        if (bucket == null) {
            bucket = buckets[MyHash(key)] = createBucket();
        }
        Node<K, V> node = getFromBucket(bucket, key);
        if (node == null) {
            size++;
            bucket.add(createNode(key, value));
            keySet.add(key);
        } else {
            node.value = value;
        }
    }

    @Override
    public Set<K> keySet() {
        return keySet;
    }

    /** Deletes a node from bucket. */
    private V deleteFromBucket(Collection<Node<K, V>> bucket, Node<K, V> node) {
        if (node == null) {
            return null;
        }
        V value = node.value;
        size--;
        keySet.remove(node.key);
        bucket.remove(node);
        return value;
    }

    @Override
    public V remove(K key) {
        Collection<Node<K, V>> bucket = buckets[MyHash(key)];
        if (bucket == null) {
            return null;
        }
        Node<K, V> node = getFromBucket(bucket, key);
        return deleteFromBucket(bucket, node);
    }

    @Override
    public V remove(K key, V value) {
        Collection<Node<K, V>> bucket = buckets[MyHash(key)];
        if (bucket == null) {
            return null;
        }
        Node<K, V> node = getFromBucket(bucket, key);
        if (node.value.equals(value)) {
            return deleteFromBucket(bucket, node);
        } else {
            return null;
        }
    }

    @Override
    public Iterator<K> iterator() {
        return keySet.iterator();
    }
}
