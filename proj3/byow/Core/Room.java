package byow.Core;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

/**
 * Class for generating rooms and hallways in world.
 */
public class Room {
    /** Creates a room whose bottom-left corner is at (x, y) of given width and height.
     *  Returns the number of newly tessellated tiles. */
    static int createRoom(TETile[][] world, int x, int y, int width, int height) {
        if (!verifyRoom(world, x, y, width, height)) {
            return 0;
        }

        return
    }

    /** Verifies if a room can be created. */
    static boolean verifyRoom(TETile[][] world, int x, int y, int width, int height) {}

    /** Fills the rectangular area with floor tile and returns newly filled tiles number. */
    static int fill(TETile[][] world, int x, int y, int width, int height) {

    }
}
