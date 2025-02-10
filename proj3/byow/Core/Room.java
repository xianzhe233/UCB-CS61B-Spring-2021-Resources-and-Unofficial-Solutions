package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

/**
 * Class for generating rooms and hallways in world.
 */
public class Room {
    /**
     * Creates a room whose bottom-left corner is at (x, y) of given width and height.
     * Returns the number of newly tessellated tiles.
     */
    static int createRoom(TETile[][] world, int x, int y, int width, int height) {
        if (!verifyRoom(world, x, y, width, height)) {
            return 0;
        }
        return fill(world, x, y, width, height, Tileset.FLOOR);
    }

    /**
     * Verifies if a room can be created.
     */
    static boolean verifyRoom(TETile[][] world, int x, int y, int width, int height) {
        if ((x + width > World.WIDTH) || (y + height > World.HEIGHT)) {
            return false;
        }
        for (int row = x - 1; row <= x + width; row++) {
            for (int col = y - 1; col <= y + height; col++) {
                if (row < 0 || row >= World.WIDTH || col < 0 || col >= World.HEIGHT) {
                    continue;
                }
                if (world[row][col] == Tileset.FLOOR) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Fills the tile at (x, y). If that tile is not NOTHING, just return false.
     */
    static boolean fill(TETile[][] world, int x, int y, TETile tile) {
        if (world[x][y] != Tileset.NOTHING) {
            return false;
        }
        world[x][y] = tile;
        return true;
    }

    /**
     * Fills a rectangular area from (x, y) with tile,
     * returns number of newly tessellated tiles.
     */
    static int fill(TETile[][] world, int x, int y, int width, int height, TETile tile) {
        int cnt = 0;
        for (int row = x; row < x + width; row++) {
            if (row >= World.WIDTH || row < 0) {
                break;
            }
            for (int col = y; col < y + height; col++) {
                if (col >= World.HEIGHT || col < 0) {
                    break;
                }
                if (fill(world, row, col, tile)) {
                    cnt += 1;
                }
            }
        }
        return cnt;
    }
}
