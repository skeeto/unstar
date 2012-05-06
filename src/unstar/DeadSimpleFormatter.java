package unstar;

import java.util.logging.Formatter;
import java.util.logging.LogRecord;

/**
 * Simpler than the not-so-simple SimpleFormatter.
 */
final class DeadSimpleFormatter extends Formatter {

    /** Singleton instance of this formatter. */
    private static final Formatter INSTANCE = new DeadSimpleFormatter();

    /**
     * Hidden constructor.
     */
    private DeadSimpleFormatter() {
    }

    /**
     * Get the singleton.
     * @return the singleton
     */
    public static Formatter get() {
        return INSTANCE;
    }

    @Override
    public String format(final LogRecord record) {
        return record.getLevel() + ": " +  record.getMessage() + "\n";
    }
}
