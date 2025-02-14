package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import byow.Core.World;
import edu.princeton.cs.introcs.StdDraw;

import java.awt.*;
import java.io.File;

import static byow.Core.FileUtils.readObject;
import static byow.Core.FileUtils.writeObject;
import static byow.Core.Parameters.*;
import static byow.Core.World.createWorld;

public class Engine {
    /* Feel free to change the width and height. */
    public static final int WIDTH = 120;
    public static final int HEIGHT = 60;
    TERenderer ter = new TERenderer();
    boolean gamestarted = false;
    boolean newWorld = false;
    boolean colonDown = false;
    TETile[][] world;
    int avatarX;
    int avatarY;

    /**
     * Method used for exploring a fresh world. This method should handle all inputs,
     * including inputs from the main menu.
     */
    public void interactWithKeyboard() {
        String input = menu();
        if (newWorld) {
            world = createWorld(extractSeed(input.toString()));
        }
        locateAvatar();
        StdDraw.pause(500);
        ter.initialize(WIDTH, HEIGHT);
        ter.renderFrame(world);
        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                char c = StdDraw.nextKeyTyped();
                interactWith(c);
                ter.renderFrame(world);
            }
        }

    }

    public void interactWith(char c) {
        c = Character.toLowerCase(c);
        if (colonDown) {
            if (c == quitKey) {
                saveWorld();
                System.exit(0);
            }
            colonDown = false;
        }
        if (c == commandkey) {
            colonDown = true;
        } else if (c == leftkey || c == rightkey || c == upkey || c == downkey) {
            move(c);
        }
    }

    public void locateAvatar() {
        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                if (world[x][y].equals(Tileset.AVATAR)) {
                    avatarX = x;
                    avatarY = y;
//                    System.out.println("Avatar located at: " + x + ", " + y);
                    return;
                }
            }
        }
    }

    public void move(char moveKey) {
        int direction = directionOf(moveKey);
        int nx = avatarX + dx[direction];
        int ny = avatarY + dy[direction];
        if (verifyMove(nx, ny)) {
            world[avatarX][avatarY] = Tileset.FLOOR;
            world[nx][ny] = Tileset.AVATAR;
            avatarX = nx;
            avatarY = ny;
        }
    }

    public int directionOf(char moveKey) {
        switch (moveKey) {
            case 'w':
                return 0;
            case 'd':
                return 1;
            case 's':
                return 2;
            case 'a':
                return 3;
            default:
                return -1;
        }
    }

    public boolean verifyMove(int x, int y) {
        return World.insideWorld(x, y) && world[x][y].equals(Tileset.FLOOR);
    }

    /** Processes the menu part, returns input string. */
    public String menu() {
        initMenu();
        StringBuilder input = new StringBuilder();
        while (!gamestarted) {
            drawMenu();
            input.append(buildMenuInput());
            drawMenuInput(input.toString());
            StdDraw.show();
        }
        return input.toString();
    }

    public void initMenu() {
        StdDraw.setCanvasSize(WIDTH * 16, HEIGHT * 16);
        StdDraw.setXscale(0, WIDTH);
        StdDraw.setYscale(0, HEIGHT);
        StdDraw.clear(Color.BLACK);
        StdDraw.enableDoubleBuffering();
    }

    /**
     * Draws plain menu.
     */
    public void drawMenu() {
        StdDraw.clear(Color.BLACK);
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.setFont(menuTitleFont);
        StdDraw.text(WIDTH / 2, menuTitlePos, "CS61B: BYoW");

        StdDraw.setFont(menuOptionFont);
        StdDraw.text(WIDTH / 2, menuOptionPos1, "New Game (N)");
        StdDraw.text(WIDTH / 2, menuOptionPos2, "Load Game (L)");
        StdDraw.text(WIDTH / 2, menuOptionPos3, "Quit (Q)");
    }

    public String buildMenuInput() {
        StringBuilder sb = new StringBuilder();
        while (StdDraw.hasNextKeyTyped()) {
            char c = StdDraw.nextKeyTyped();
            if (c == quitKey) {
                System.exit(0);
            } else if (c == newGameKey) {
                if (newWorld || gamestarted) {
                    continue;
                }
                sb.append(Character.toUpperCase(newGameKey));
                newWorld = true;
            } else if (c == loadGameKey) {
                if (newWorld) {
                    continue;
                }
                sb.append(Character.toUpperCase(loadGameKey));
                loadWorld();
                gamestarted = true;
            } else if (c == savekey) {
                if (!newWorld || gamestarted) {
                    continue;
                }
                sb.append(Character.toUpperCase(savekey));
                gamestarted = true;
            } else if (Character.isDigit(c)) {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    public void drawMenuInput(String s) {
        StdDraw.text(WIDTH / 2, menuInputPos, s);
    }

    public long extractSeed(String s) {
        s = s.toLowerCase();
        int nPos = s.indexOf("n");
        int sPos = s.indexOf("s");
        try {
            return Long.parseLong(s.substring(nPos + 1, sPos));
        } catch (IndexOutOfBoundsException e) {
            return -1l;
        }
    }

    /**
     * Method used for autograding and testing your code. The input string will be a series
     * of characters (for example, "n123sswwdasdassadwas", "n123sss:q", "lwww". The engine should
     * behave exactly as if the user typed these characters into the engine using
     * interactWithKeyboard.
     * <p>
     * Recall that strings ending in ":q" should cause the game to quite save. For example,
     * if we do interactWithInputString("n123sss:q"), we expect the game to run the first
     * 7 commands (n123sss) and then quit and save. If we then do
     * interactWithInputString("l"), we should be back in the exact same state.
     * <p>
     * In other words, both of these calls:
     * - interactWithInputString("n123sss:q")
     * - interactWithInputString("lww")
     * <p>
     * should yield the exact same world state as:
     * - interactWithInputString("n123sssww")
     *
     * @param input the input string to feed to your program
     * @return the 2D TETile[][] representing the state of the world
     */
    public TETile[][] interactWithInputString(String input) {
        // passed in as an argument, and return a 2D tile representation of the
        // world that would have been drawn if the same inputs had been given
        // to interactWithKeyboard().
        //
        // See proj3.byow.InputDemo for a demo of how you can make a nice clean interface
        // that works for many different input types.

        input = input.toLowerCase();
        long seed = extractSeed(input);
        // if seed is -1, then it is not a new game
        if (seed != -1) {
            world = createWorld(seed);
            input = input.substring(input.indexOf("s") + 1);
        } else {
            if (input.contains("l")) {
                loadWorld();
                if (input.equals("l")) {
                    return world;
                }
                input = input.substring(input.indexOf("l") + 1);
            } else {
                return null;
            }
        }

        locateAvatar();
        for (int i = 0; i < input.length(); i++) {
            interactWith(input.charAt(i));
        }

        saveWorld();
        return world;
    }

    private void saveWorld() {
        writeObject(worldFile, world);
    }

    private void loadWorld() {
        world = readObject(worldFile, TETile[][].class);
        locateAvatar();
    }

    private void print() {
        for (int i = 0; i < HEIGHT; i++) {
            for (int j = 0; j < WIDTH; j++) {
                System.out.print(world[j][i].character());
            }
            System.out.println();
        }
    }
}
