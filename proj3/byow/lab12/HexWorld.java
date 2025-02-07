package byow.lab12;
import org.junit.Test;
import static org.junit.Assert.*;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.Random;

/**
 * Draws a world consisting of hexagonal regions.
 */
public class HexWorld {

    private static final int WIDTH = 28;
    private static final int HEIGHT = 30;

    static public void main(String[] args) {
        TERenderer ter = new TERenderer();
        ter.initialize(WIDTH, HEIGHT);
        TETile[][] world = new TETile[WIDTH][HEIGHT];
        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                world[x][y] = Tileset.NOTHING;
            }
        }

        drawHexagon(5, 3, 3, Tileset.FLOWER, world);
        drawHexagon(15, 3, 3, Tileset.MOUNTAIN, world);
        drawHexagon(15, 9, 3, Tileset.TREE, world);
        drawHexagon(15, 15, 3, Tileset.SAND, world);
        drawHexagon(15, 21, 3, Tileset.FLOWER, world);
        drawHexagon(10, 0, 3, Tileset.MOUNTAIN, world);
        drawHexagon(10, 6, 3, Tileset.MOUNTAIN, world);
        drawHexagon(10, 12, 3, Tileset.MOUNTAIN, world);
        drawHexagon(10, 18, 3, Tileset.MOUNTAIN, world);
        drawHexagon(10, 24, 3, Tileset.TREE, world);
        drawHexagon(0, 6, 3, Tileset.GRASS, world);
        drawHexagon(0, 12, 3, Tileset.GRASS, world);
        drawHexagon(0, 18, 3, Tileset.MOUNTAIN, world);
        drawHexagon(5, 9, 3, Tileset.MOUNTAIN, world);
        drawHexagon(5, 15, 3, Tileset.MOUNTAIN, world);
        drawHexagon(5, 21, 3, Tileset.GRASS, world);
        drawHexagon(20, 6, 3, Tileset.SAND, world);
        drawHexagon(20, 12, 3, Tileset.TREE, world);
        drawHexagon(20, 18, 3, Tileset.FLOWER, world);

        ter.renderFrame(world);
    }

    /**
     * Returns the width of a hexagon of size.
     */
    static int width(int size) {
        return 3 * size - 2;
    }

    /**
     * Returns the height of a hexagon of size.
     */
    static int height(int size) {
        return 2 * size;
    }

    /**
     * Returns manhattan distance of two points.
     */
    static int manhattanDistance(int x1, int y1, int x2, int y2) {
        return Math.abs(x1 - x2) + Math.abs(y1 - y2);
    }

    /**
     * Verifies if this tile should be tessellated.
     * (rx, ry) are the relative deviation from start point.
     */
    static boolean verify(int size, int rx, int ry) {
        int[] checkX = new int[]{0, width(size) - 1};
        int[] checkY = new int[]{0, height(size) - 1};

        for (int i = 0; i < checkX.length; i++) {
            for (int j = 0; j < checkY.length; j++) {
                int cx = checkX[i];
                int cy = checkY[j];
                if (manhattanDistance(cx, cy, rx, ry) < size - 1) {
                    // A skipped tile must have a manhattan distance from
                    // the corners of the square area smaller than size - 1.
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * Draws a hexagon with tile at given coordinate in given world.
     * (x, y) represents the bottom-left corner of the square that circumscribes the hexagon. (start point)
     */
    static void drawHexagon(int x, int y, int size, TETile tile, TETile[][] world) {
        final int width = width(size);
        final int height = height(size);
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                if (verify(size, i, j)) {
                    world[x + i][y + j] = tile;
                }
            }
        }
    }
}
