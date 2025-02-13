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
    private static final double ROOM_AREA_RATE = 0.8;
    private static final double FINAL_AREA_RATE = 0.9;
    private static final double TRIGGER_INCREMENT = 0.001;


    public static TETile[][] createWorld(long seed) {
        TETile[][] world = new TETile[WIDTH][HEIGHT];
        worldInit(world);

        Random rand = new Random(seed);

        createRooms(world, rand);

        DisjointSet ds = new DisjointSet(WIDTH, HEIGHT);

        createHallways(world, rand, ds);

//        cleanUp(world, ds);
        buildWalls(world);

        putAvatar(world, rand);

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
        double trigger = 0.0;
        while (currentArea < (ROOM_AREA_RATE - trigger) * AREA) {
            int x = uniform(rand, 0, WIDTH);
            int y = uniform(rand, 0, HEIGHT);
            int width = (int) (uniform(rand, 0.15, 0.35) * MIN_LENGTH);
            int height = (int) (uniform(rand, 0.15, 0.35) * MIN_LENGTH);
            int newCreated = createRoom(world, x, y, width, height);
            if (newCreated == 0) {
                trigger += TRIGGER_INCREMENT;
            }
            currentArea += newCreated;
        }
    }

    private static void createHallways(TETile[][] world, Random rand, DisjointSet ds) {
        connectAllRooms(world, ds);
        double trigger = 0.0;
        while (ds.getMaxComponentSize() < (FINAL_AREA_RATE - trigger) * AREA) {
            int[] leastCoordinates = ds.getMinComponentPoint(world, rand);
            int x = leastCoordinates[0];
            int y = leastCoordinates[1];
            trigger += TRIGGER_INCREMENT;
            if (x == 0 || x == WIDTH - 1 || y == 0 || y == HEIGHT - 1) {
                continue;
            }
            int direction = uniform(rand, 0, 4); // 0: up, 1: right, 2: down, 3: left
            int[] deviation = scan(world, x, y, direction, ds);
            if (deviation == null) {
                continue;
            }
            trigger -= TRIGGER_INCREMENT;
            int dx = deviation[0];
            int dy = deviation[1];
            int width, height;
            if (dx == 0) {
                width = uniform(rand, 1, 3);
                height = Math.abs(dy);
            } else {
                width = Math.abs(dx);
                height = uniform(rand, 1, 3);
            }
            if (dx < 0 || dy < 0) {
                x += dx;
                y += dy;
            }
            fill(world, x, y, width, height, Tileset.FLOOR);
            connectAllRooms(world, ds);
        }
    }

    /** Scans the world to see if this hallway can connect two components.
     *  If it can, Returns the deviation vector. If cannot, return null. */
    private static int[] scan(TETile[][] world, int x, int y, int direction, DisjointSet ds) {
        int[] dx = {0, 1, 0, -1};
        int[] dy = {1, 0, -1, 0};
        int nx = x, ny = y;
        while (insideWorld(nx, ny)) {
            nx += dx[direction];
            ny += dy[direction];
            if (!insideWorld(nx, ny)) {
                return null;
            }
            if (world[nx][ny] == Tileset.NOTHING) {
                continue;
            }
            if (!ds.isConnected(x, y, nx, ny)) {
                return new int[]{nx - x, ny - y};
            }
        }
        return null;
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
                if (insideWorld(nx, ny) && !ds.isConnected(x, y, nx, ny)
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
                        if (insideWorld(nx, ny) && world[nx][ny] == Tileset.NOTHING) {
                            world[nx][ny] = Tileset.WALL;
                        }
                    }
                }
            }
        }
    }

    private static void putAvatar(TETile[][] world, Random rand) {
        int x, y;
        while (true) {
            x = uniform(rand, 0, WIDTH);
            y = uniform(rand, 0, HEIGHT);
            if (world[x][y] == Tileset.FLOOR) {
                world[x][y] = Tileset.AVATAR;
                break;
            }
        }
    }

    public static boolean insideWorld(int x, int y) {
        return x >= 0 && x < WIDTH && y >= 0 && y < HEIGHT;
    }

    public static void main(String[] args) {
        TERenderer ter = new TERenderer();
        ter.initialize(WIDTH, HEIGHT);
        TETile[][] world = createWorld(new Random().nextInt());
        ter.renderFrame(world);
    }
}
