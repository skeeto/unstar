package unstar;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.NoSuchElementException;
import lombok.SneakyThrows;
import lombok.extern.java.Log;

/**
 * Provide codes from a file.
 */
@Log
public final class FileCodeProvider implements CodeProvider {

    /** The file reader for reading codes. */
    private final BufferedReader reader;

    /** The next code. */
    private String next = null;

    /**
     * Create a new provider for the given file.
     * @param in  the file to read codes from
     * @throws FileNotFoundException for the given file
     */
    @SneakyThrows
    public FileCodeProvider(final File in) throws FileNotFoundException {
        reader = new BufferedReader(new FileReader(in));
        next = reader.readLine();
    }

    @Override
    @SneakyThrows
    public String next() {
        String ret = next;
        next = reader.readLine();
        if (ret == null) {
            throw new NoSuchElementException();
        }
        return ret;
    }

    @Override
    public boolean hasNext() {
        return next != null;
    }

    @Override
    public void report(final String code) {
        log.info("valid: " + code);
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
}
