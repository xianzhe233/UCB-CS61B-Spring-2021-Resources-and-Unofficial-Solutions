package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

import static byow.Core.RandomUtils.uniform;
import static byow.Core.Room.createRoom;
import static byow.Core.Room.fill;

/**
 * Class for generating worlds of the game.
 */
public class World {
    public static final int WIDTH = Engine.WIDTH;
    public static final int HEIGHT = Engine.HEIGHT;
    private static final int AREA = WIDTH * HEIGHT;
    private static final int MIN_LENGTH = WIDTH > HEIGHT ? HEIGHT : WIDTH;
    private static final double ROOM_AREA_RATE = 0.4;
    private static final double FINAL_AREA_RATE = 0.5;

    public static TETile[][] createWorld(int seed) {
        TETile[][] world = new TETile[WIDTH][HEIGHT];
        worldInit(world);

        Random rand = new Random(seed);

        createRooms(world, rand);

        DisjointSet ds = new DisjointSet(WIDTH, HEIGHT);

        createHallways(world, rand, ds);

        cleanUp(world, ds);
        buildWalls(world);

        return world;
    }

    private static void worldInit(TETile[][] world) {
        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                world[x][y] = Tileset.NOTHING;
            }
        }
    }

    private static void createRooms(TETile[][] world, Random rand) {
        int currentArea = 0;
        while ((double) currentArea / (double) AREA < ROOM_AREA_RATE) {
            int x = uniform(rand, 0, WIDTH);
            int y = uniform(rand, 0, HEIGHT);
            int width = (int) (uniform(rand, 0.15, 0.35) * MIN_LENGTH);
            int height = (int) (uniform(rand, 0.15, 0.35) * MIN_LENGTH);
            currentArea += createRoom(world, x, y, width, height);
            System.out.println(width + " " + height);
        }
    }

    private static void createHallways(TETile[][] world, Random rand, DisjointSet ds) {
        connectAllRooms(world, ds);

        while (ds.getMaxComponentSize() < FINAL_AREA_RATE * AREA) {
            int[] leastCoordinates = ds.getMinComponentPoint(rand);
            int x = leastCoordinates[0];
            int y = leastCoordinates[1];
            System.out.println(x + " " + y);
            int width;
            int height;
            boolean direction = rand.nextBoolean(); // true for horizontal, false for vertical
            boolean positive = rand.nextBoolean(); // true for positive, false for negative
            if (direction) {
                height = uniform(rand, 3, 5);
                width = (int) (uniform(rand, 0.2, 0.5) * World.WIDTH);
            } else {
                width = uniform(rand, 3, 5);
                height = (int) (uniform(rand, 0.2, 0.5) * World.HEIGHT);
            }
            if (positive) {
                x -= 1;
                y -= 1;
            } else {
                x -= width;
                y -= height;
            }
            fill(world, x, y, width, height, Tileset.FLOOR);
            connectAllRooms(world, ds);
        }
    }

    /**
     * Connects tiles in the room of (x, y).
     */
    private static void connectRoom(TETile[][] world, DisjointSet ds, int sx, int sy) {
        int[] dx = {0, 0, 1, -1};
        int[] dy = {1, -1, 0, 0};
        Queue<Integer> xqueue = new LinkedList<>();
        Queue<Integer> yqueue = new LinkedList<>();
        xqueue.add(sx);
        yqueue.add(sy);
        while (!xqueue.isEmpty()) {
            int x = xqueue.poll();
            int y = yqueue.poll();
            for (int i = 0; i < 4; i++) {
                int nx = x + dx[i];
                int ny = y + dy[i];
                if (nx >= 0 && nx < WIDTH && ny >= 0 && ny < HEIGHT && !ds.isConnected(x, y, nx, ny)
                        && world[nx][ny] == Tileset.FLOOR) {
                    ds.union(x, y, nx, ny);
                    xqueue.add(nx);
                    yqueue.add(ny);
                }
            }
        }
    }

    private static void connectAllRooms(TETile[][] world, DisjointSet ds) {
        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                if (world[x][y] == Tileset.FLOOR) {
                    connectRoom(world, ds, x, y);
                }
            }
        }
    }

    private static void cleanUp(TETile[][] world, DisjointSet ds) {
        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                if (ds.getSize(x, y) < FINAL_AREA_RATE * AREA) {
                    world[x][y] = Tileset.NOTHING;
                }
            }
        }
    }

    private static void buildWalls(TETile[][] world) {
        int[] dx = {0, 0, 1, -1, 1, -1, 1, -1};
        int[] dy = {1, -1, 0, 0, 1, -1, -1, 1};
        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                if (world[x][y] == Tileset.FLOOR) {
                    if (x == 0 || x == WIDTH - 1 || y == 0 || y == HEIGHT - 1) {
                        world[x][y] = Tileset.WALL;
                        continue;
                    }
                    for (int i = 0; i < 8; i++) {
                        int nx = x + dx[i];
                        int ny = y + dy[i];
                        if (nx >= 0 && nx < WIDTH && ny >= 0 && ny < HEIGHT && world[nx][ny] == Tileset.NOTHING) {
                            world[nx][ny] = Tileset.WALL;
                        }
                    }
                }
            }
        }


    }

    public static void main(String[] args) {
        TERenderer ter = new TERenderer();
        ter.initialize(WIDTH, HEIGHT);
        TETile[][] world = createWorld(new Random().nextInt());
        ter.renderFrame(world);
    }
}
