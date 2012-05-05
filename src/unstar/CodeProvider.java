package unstar;

import java.util.Iterator;

/**
 * Provides a series of codes to test.
 */
public interface CodeProvider extends Iterator<String> {

    /**
     * Report a successful code to the provider, to log.
     * @param code  a good code
     */
    void report(String code);
}
