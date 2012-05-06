package unstar.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.util.Scanner;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import lombok.SneakyThrows;
import lombok.extern.java.Log;

/**
 * Implements a server for distributing codes to clients to test.
 */
@Log
public final class Server implements Runnable {

    /** Port to listen on. */
    private final int port;

    /** Source of codes. */
    private final Scanner source;

    /** Report stream for reporting good codes. */
    private final PrintStream report;

    /** Executor for launching new client handlers (Dispensers). */
    private final Executor exec = Executors.newCachedThreadPool();

    /**
     * Create a new server for a given set of codes.
     * @param file    the file containing codes
     * @param port    the port to listen on
     * @param report  the channel for reporting good codes
     * @throws FileNotFoundException for the code source file
     */
    @SneakyThrows
    public Server(final File file, final int port, final PrintStream report)
        throws FileNotFoundException {
        source = new Scanner(new FileInputStream(file));
        this.port = port;
        this.report = report;
    }

    @Override
    public void run() {
        ServerSocket server = null;
        try {
            server = new ServerSocket(port);
        } catch (IOException e) {
            log.severe("Could not initiate server.");
            return;
        }
        while (true) {
            try {
                exec.execute(new Dispenser(server.accept(), source, report));
            } catch (IOException e) {
                log.warning("IO error on accept(): " + e);
            }
        }
    }
}
