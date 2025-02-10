package byow.Core;
import byow.TileEngine.TETile;

import static byow.Core.RandomUtils.*;
import java.util.Random;

public class DisjointSet {
    private int[] parent;
    private int[] size;
    private int width;

    public DisjointSet(int width, int height) {
        this.width = width;
        int size = width * height;
        parent = new int[size];
        this.size = new int[size];
        for (int i = 0; i < size; i++) {
            parent[i] = i;
            this.size[i] = 1;
        }
    }

    private int toIndex(int x, int y) {
        return y * width + x;
    }

    private int[] toCoordinates(int index) {
        return new int[]{index % width, index / width};
    }

    public int find(int x, int y) {
        int index = toIndex(x, y);
        if (parent[index] != index) {
            parent[index] = find(parent[index] % width, parent[index] / width); // Path compression
        }
        return parent[index];
    }

    public void union(int x1, int y1, int x2, int y2) {
        int root1 = find(x1, y1);
        int root2 = find(x2, y2);

        if (root1 != root2) {
            if (size[root1] > size[root2]) {
                parent[root2] = root1;
                size[root1] += size[root2];
            } else {
                parent[root1] = root2;
                size[root2] += size[root1];
            }
        }
    }

    public boolean isConnected(int x1, int y1, int x2, int y2) {
        return find(x1, y1) == find(x2, y2);
    }

    public int getSize(int x, int y) {
        int root = find(x, y);
        return size[root];
    }

    public int getMinComponentSize() {
        int minSize = Integer.MAX_VALUE;
        for (int i = 0; i < parent.length; i++) {
            if (parent[i] == i && size[i] < minSize && size[i] > 1) {
                minSize = size[i];
            }
        }
        return minSize;
    }

    public int getMaxComponentSize() {
        int maxSize = Integer.MIN_VALUE;
        for (int i = 0; i < parent.length; i++) {
            if (parent[i] == i && size[i] > maxSize) {
                maxSize = size[i];
            }
        }
        return maxSize;
    }

    public int[] getMinComponentPoint(TETile[][] world, Random rand) {
        int minSize = getMinComponentSize();
        while (true) {
            int i = uniform(rand, 0, parent.length);
            if (size[find(i % width, i / width)] == minSize) {
                return toCoordinates(i);
            }
        }
    }
}