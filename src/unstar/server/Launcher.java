package unstar.server;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

/**
 * The main class for launching a server.
 */
public final class Launcher {

    /**
     * Hidden constructor.
     */
    private Launcher() {
    }

    /**
     * The main method, launches a server.
     * @param args  command line arguments
     */
    public static void main(final String[] args) {
        if (args.length != 1) {
            System.err.println("error: select one file to serve");
            System.exit(-1);
        }

        unstar.DeadSimpleFormatter.load();

        try {
            File log = new File("valid.log");
            FileOutputStream fout = new FileOutputStream(log, true);
            PrintStream out = new PrintStream(fout, true);
            File in = new File(args[0]);
            new Server(in, Server.DEFAULT_PORT, out).run();
        } catch (FileNotFoundException e) {
            System.err.println("File not found: " + e.getMessage());
            System.exit(-1);
        }
    }
}
