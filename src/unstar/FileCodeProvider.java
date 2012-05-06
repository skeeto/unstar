package unstar;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import lombok.Delegate;
import lombok.extern.java.Log;

/**
 * Provide codes from a stream.
 */
@Log
public final class FileCodeProvider implements CodeProvider {

    /** Delegate input stream provider. */
    @Delegate
    private final CodeProvider delegate;

    /**
     * Create a new provider for the given file.
     * @param in  the file to read codes from
     * @throws FileNotFoundException for the given file
     */
    public FileCodeProvider(final File in) throws FileNotFoundException {
        delegate = new InputStreamCodeProvider(new FileInputStream(in));
    }
}
