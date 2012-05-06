package unstar.server;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.NoSuchElementException;
import java.util.Scanner;
import lombok.extern.java.Log;

/**
 * Talks to a single client code checker.
 */
@Log
public final class Dispenser implements Runnable {

    /** Source of codes. */
    private Scanner source;

    /** Input from client. */
    private Scanner in;

    /** Output to client. */
    private PrintStream out;

    /** Report stream for reporting good codes. */
    private PrintStream report;

    /**
     * Create a new dispenser to dealing with a client.
     * @param client  the client socket
     * @param source  the source of codes
     * @param report  the good code reporting stream
     * @throws IOException if the connection failed to start
     */
    public Dispenser(final Socket client, final Scanner source,
                     final PrintStream report)
        throws IOException {
        in = new Scanner(client.getInputStream());
        out = new PrintStream(client.getOutputStream());
        this.source = source;
        this.report = report;
    }

    @Override
    public void run() {
        try {
            while (!out.checkError()) {
                String command = in.nextLine();
                if ("*next".equals(command)) {
                    String code = unstar.InputStreamCodeProvider.TERMINATOR;
                    synchronized (source) {
                        if (source.hasNextLine()) {
                            code = source.nextLine();
                        }
                    }
                    log.info("Dispensing " + code);
                    out.println(code);
                } else {
                    report.println(command);
                }
            }
        } catch (NoSuchElementException e) {
            log.info("client disconnected");
        }
    }
}
