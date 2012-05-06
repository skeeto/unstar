package unstar;

import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/**
 * Simpler than the not-so-simple SimpleFormatter.
 */
public final class DeadSimpleFormatter extends Formatter {

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

    /**
     * Load the DeadSimpleFormatter into all handlers.
     */
    public static void load() {
        for (Handler h : Logger.getLogger("").getHandlers()) {
            h.setFormatter(DeadSimpleFormatter.get());
        }
    }
}
