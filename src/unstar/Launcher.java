package unstar;

import java.awt.AWTException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Logger;

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
        /* Command line parsing. */
        if (args.length != 1) {
            System.out.println("Usage: unstar [file]");
            System.exit(-1);
        }

        /* Set up the logger. */
        try {
            Handler handler = new FileHandler("cracker.log");
            Logger.getLogger("").addHandler(handler);
            for (Handler h : Logger.getLogger("").getHandlers()) {
                h.setFormatter(DeadSimpleFormatter.get());
            }
        } catch (IOException e) {
            System.out.println("Failed to create log file: " + e.getMessage());
            System.exit(-1);
        }

        /* Start testing codes. */
        try {
            new Cracker(new FileCodeProvider(new File(args[0]))).run();
        } catch (AWTException e) {
            System.err.println(e);
            System.exit(-1);
        } catch (FileNotFoundException e) {
            System.err.println(e);
            System.exit(-1);
        }
    }
}
