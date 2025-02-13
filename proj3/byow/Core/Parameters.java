package byow.Core;

import java.awt.*;

import static byow.Core.Engine.HEIGHT;

public class Parameters {
    /** Fonts */
    static Font menuTitleFont = new Font("JetBrains Mono", Font.BOLD, 50);
    static Font menuOptionFont = new Font("JetBrains Mono", Font.PLAIN, 30);

    /** Menu positions */
    static int menuTitlePos = HEIGHT * 3 / 4 - 1;
    static int menuOptionPos1 = HEIGHT / 2;
    static int menuOptionPos2 = HEIGHT / 2 - 3;
    static int menuOptionPos3 = HEIGHT / 2 - 6;
    static int menuInputPos = HEIGHT / 2 - 9;

    /** Key bindings */
    static char newGameKey = 'n';
    static char loadGameKey = 'l';
    static char quitKey = 'q';
    static char savekey = 's';
    static char leftkey = 'a';
    static char rightkey = 'd';
    static char upkey = 'w';
    static char downkey = 's';
    static char commandkey = ':';

    /** Moving vectors. */
    static int[] dx = {0, 1, 0, -1};
    static int[] dy = {1, 0, -1, 0};
}
