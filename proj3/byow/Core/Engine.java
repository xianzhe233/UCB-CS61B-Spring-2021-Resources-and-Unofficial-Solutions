package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import edu.princeton.cs.introcs.StdDraw;

import java.awt.*;
import java.io.File;

import static byow.Core.FileUtils.readObject;
import static byow.Core.FileUtils.writeObject;
import static byow.Core.Parameters.*;
import static byow.Core.World.createWorld;

public class Engine {
    /* Feel free to change the width and height. */
    public static final int WIDTH = 80;
    public static final int HEIGHT = 60;
    TERenderer ter = new TERenderer();
    boolean gamestarted;
    TETile[][] world;

    public Engine() {
        ter.initialize(WIDTH, HEIGHT);
        gamestarted = false;
    }

    /**
     * Method used for exploring a fresh world. This method should handle all inputs,
     * including inputs from the main menu.
     */
    public void interactWithKeyboard() {
        menu();
    }

    public void menu() {
        initMenu();
        StringBuilder input = new StringBuilder();
        while (!gamestarted) {
            drawMenu();
            input.append(buildMenuInput());
            drawMenuInput(input.toString());
            StdDraw.show();
        }
        StdDraw.pause(500);
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
                sb.append(Character.toUpperCase(newGameKey));
            } else if (c == loadGameKey) {
                sb.append(Character.toUpperCase(loadGameKey));
                gamestarted = true;
            } else if (c == savekey) {
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

        TETile[][] finalWorldFrame;

        input = input.toLowerCase();
        int nPos = input.indexOf("n");
        int sPos = input.indexOf("s");
        long seed = Long.parseLong(input.substring(nPos + 1, sPos));
        finalWorldFrame = createWorld(seed);

        return finalWorldFrame;
    }

    private void saveWorld(TETile[][] world) {
        File worldFile = new File("world.txt");
        writeObject(worldFile, world);
    }

    private TETile[][] loadWorld() {
        File worldFile = new File("world.txt");
        return readObject(worldFile, TETile[][].class);
    }
}
