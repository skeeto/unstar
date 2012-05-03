package unstar;

import java.awt.AWTException;
import java.io.IOException;

/**
 * The main class.
 */
public final class Launcher {

    /**
     * Hidden constructor.
     */
    private Launcher() {
    }

    /**
     * The main method.
     * @param args  the command line arguments
     */
    public static void main(final String[] args) {
        String prefix = "";
        String start = "";
        if (args.length > 0) {
            prefix = args[0];
        }
        if (args.length > 1) {
            start = args[1];
        }
        try {
            new Cracker(prefix, start).run();
        } catch (AWTException e) {
            System.err.println(e);
            System.exit(-1);
        } catch (IOException e) {
            System.err.println(e);
            System.exit(-1);
        }
    }
}
