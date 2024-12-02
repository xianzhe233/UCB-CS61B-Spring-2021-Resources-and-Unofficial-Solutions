package gh2;

import edu.princeton.cs.algs4.StdAudio;
import edu.princeton.cs.algs4.StdDraw;

/**
 * A client that uses the synthesizer package to replicate a plucked guitar string sound
 */
public class GuitarHero {
    public static final double CONCERT_A = 440.0;
    private static String keyboard = "q2we4r5ty7u8i9op-[=zxdcfvgbnjmk,.;/' ";
    private static GuitarString[] strings = new GuitarString[keyboard.length()];

    public static void main(String[] args) {
        stringInit();

        while (true) {
            /* check if the user has typed a key; if so, process it */
            if (StdDraw.hasNextKeyTyped()) {
                char key = StdDraw.nextKeyTyped();
                int keyIndex = keyboard.indexOf(key);
                if (keyIndex != -1 && keyIndex < keyboard.length()) {
                    strings[keyIndex].pluck();
                }
            }

            double sample = superpositionSample();

            /* play the sample on standard audio */
            StdAudio.play(sample);

            /* advance the simulation of each guitar string by one step */
            ticAll();
        }
    }

    public static GuitarString stringOfKey(int i) {
        return new GuitarString(CONCERT_A * Math.pow(2, (i - 24) / 12));
    }

    public static void stringInit() {
        for (int i = 0; i < keyboard.length(); i++) {
            strings[i] = stringOfKey(i);
        }
    }

    public static double superpositionSample() {
        double sample = 0;
        for (GuitarString string : strings) {
            sample += string.sample();
        }
        return sample;
    }

    public static void ticAll() {
        for (GuitarString string : strings) {
            string.tic();
        }
    }
}