package unstar;

import java.awt.AWTException;
import java.io.File;
import java.io.FileNotFoundException;

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
        if (args.length != 1) {
            System.out.println("Usage: unstar [file]");
        }
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
