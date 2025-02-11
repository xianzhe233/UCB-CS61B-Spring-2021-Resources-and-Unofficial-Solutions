package byow.lab13;

import byow.Core.RandomUtils;
import edu.princeton.cs.introcs.StdDraw;

import java.awt.Color;
import java.awt.Font;
import java.util.Random;

public class MemoryGame {
    /** The width of the window of this game. */
    private int width;
    /** The height of the window of this game. */
    private int height;
    /** The current round the user is on. */
    private int round;
    /** The Random object used to randomly generate Strings. */
    private Random rand;
    /** Whether the game is over. */
    private boolean gameOver;
    /** Whether it is the player's turn. Used in the last section of the
     * spec, 'Helpful UI'. */
    private boolean playerTurn;
    /** The characters we generate random Strings from. */
    private static final char[] CHARACTERS = "abcdefghijklmnopqrstuvwxyz".toCharArray();
    /** Encouraging phrases. Used in the last section of the spec, 'Helpful UI'. */
    private static final String[] ENCOURAGEMENT = {"You can do this!", "I believe in you!",
                                                   "You got this!", "You're a star!", "Go Bears!",
                                                   "Too easy for you!", "Wow, so impressive!"};

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Please enter a seed");
            return;
        }

        long seed = Long.parseLong(args[0]);
        MemoryGame game = new MemoryGame(40, 40, seed);
        game.startGame();
    }

    public MemoryGame(int width, int height, long seed) {
        /* Sets up StdDraw so that it has a width by height grid of 16 by 16 squares as its canvas
         * Also sets up the scale so the top left is (0,0) and the bottom right is (width, height)
         */
        this.width = width;
        this.height = height;
        this.rand = new Random(seed);
        StdDraw.setCanvasSize(this.width * 16, this.height * 16);
        Font font = new Font("Monaco", Font.BOLD, 30);
        StdDraw.setFont(font);
        StdDraw.setXscale(0, this.width);
        StdDraw.setYscale(0, this.height);
        StdDraw.clear(Color.BLACK);
        StdDraw.enableDoubleBuffering();

    }

    public String generateRandomString(int n) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < n; i++) {
            int index = RandomUtils.uniform(rand, CHARACTERS.length);
            sb.append(CHARACTERS[index]);
        }
        return sb.toString();
    }

    public void drawFrame(String s) {
        StdDraw.clear(Color.BLACK);
        StdDraw.setFont(new Font("Sans-Serif", Font.BOLD, 30));
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.text(width / 2, height / 2, s);
        StdDraw.show();
        //TODO: If game is not over, display relevant game information at the top of the screen
    }

    public void flashSequence(String letters) {
        for (int i = 0; i < letters.length(); i++) {
            char c = letters.charAt(i);
            drawFrame(Character.toString(c));
            StdDraw.pause(1000);
            drawFrame("");
            StdDraw.pause(500);
        }
    }

    public String solicitNCharsInput(int n) {
        StringBuilder sb = new StringBuilder();
        while (sb.length() < n) {
            if (StdDraw.hasNextKeyTyped()) {
                char c = StdDraw.nextKeyTyped();
                sb.append(c);
                drawFrame(sb.toString());
            }
        }
        return sb.toString();
    }

    public void startGame() {
        round = 1;
        gameOver = false;
        while (!gameOver) {
            drawFrame("Round: " + round);
            StdDraw.pause(1000);
            String randomString = generateRandomString(round);
            flashSequence(randomString);
            String userInput = solicitNCharsInput(round);
            if (!userInput.equals(randomString)) {
                gameOver = true;
                drawFrame("Game Over! You made it to round: " + round);
            } else {
                StdDraw.pause(1000);
                round++;
            }
        }
    }

}
