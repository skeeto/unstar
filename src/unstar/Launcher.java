package unstar;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;
import java.awt.AWTException;
import java.io.File;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Logger;


/**
 * The main class.
 */
public final class Launcher {

    /** Codes source file name. */
    @Parameter(names = "-f", description = "Get codes from a file.")
    private String file;

    /** Codes source host name. */
    @Parameter(names = "-s", description = "Get codes from code server.")
    private String host;

    /** Codes from generation. */
    @Parameter(names = "-g", description =
               "Get codes by generation (range format \"START:END\").")
    private String generate;

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
        Launcher params = new Launcher();
        try {
            new JCommander(params, args);
        } catch (ParameterException e) {
            System.err.println("error: " + e.getMessage());
            usage(-1);
        }
        CodeProvider provider = null;
        int pcount = 0;
        try {
            if (params.file != null) {
                provider = new FileCodeProvider(new File(params.file));
                pcount++;
            } else if (params.host != null) {
                int port = unstar.server.Server.DEFAULT_PORT;
                provider = new TcpCodeProvider(params.host, port);
                pcount++;
            } else if (params.generate != null) {
                try {
                    String start = params.generate.split(":")[0];
                    String end = params.generate.split(":")[1];
                    provider = new GeneratorCodeProvider(start, end);
                    pcount++;
                } catch (ArrayIndexOutOfBoundsException e) {
                    System.err.println("error: bad generator format");
                    usage(-1);
                }
            }

            if (pcount != 1) {
                System.err.println("error: must select exactly one source");
                usage(-1);
            }
        } catch (Exception e) {
            System.err.println("error: " + e);
            System.exit(-1);
        }

        /* Set up the logger. */
        try {
            Handler handler = new FileHandler("cracker.log");
            Logger.getLogger("").addHandler(handler);
        } catch (IOException e) {
            System.out.println("Failed to create log file: " + e.getMessage());
            System.exit(-1);
        }
        DeadSimpleFormatter.load();

        /* Start testing codes. */
        try {
            new Cracker(provider).run();
        } catch (AWTException e) {
            System.err.println(e);
            System.exit(-1);
        }
    }

    /**
     * Print usage and exit.
     * @param status  the exit status to use
     */
    private static void usage(final int status) {
        new JCommander(new Launcher()).usage();
        System.exit(status);
    }
}
