package unstar;

import java.io.InputStream;
import java.util.NoSuchElementException;
import java.util.Scanner;
import lombok.SneakyThrows;
import lombok.extern.java.Log;

/**
 * Provide codes from a file.
 */
@Log
public final class InputStreamCodeProvider implements CodeProvider {

    /** The terminator line that can end the stream early. */
    public static final String TERMINATOR = "--------";

    /** The file reader for reading codes. */
    private final Scanner scanner;

    /** Peekahead string for checking for the terminator. */
    private String peek = null;

    /** True if the terminator line has been read. */
    private boolean terminated = false;

    /**
     * Create a new provider for the given file.
     * @param in  the stream to read codes from
     */
    @SneakyThrows
    public InputStreamCodeProvider(final InputStream in) {
        scanner = new Scanner(in);
    }

    @Override
    @SneakyThrows
    public String next() {
        if (terminated) {
            throw new NoSuchElementException();
        } else if (peek != null) {
            String ret = peek;
            peek = null;
            return ret;
        } else if (!hasNext()) {
            throw new NoSuchElementException();
        }
        return next();
    }

    @Override
    public boolean hasNext() {
        if (peek != null) {
            return true;
        } else if (scanner.hasNextLine()) {
            peek = scanner.nextLine();
            terminated = peek.equals(TERMINATOR);
            return !terminated;
        } else {
            terminated = true;
            return false;
        }
    }

    @Override
    public void report(final String code) {
        log.info("VALID " + code);
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
}
