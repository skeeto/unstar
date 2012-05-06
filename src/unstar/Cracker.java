package unstar;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import lombok.extern.java.Log;

/**
 * Automates the cracking process by firing up Stars! and guessing
 * codes.
 */
@Log
public final class Cracker extends Robot implements Runnable {

    /** The command to run Stars!. */
    private static final String[] STARS_COMMAND = {"wine", "stars/stars.exe"};

    /** Location of the Stars! .ini file. */
    private static final File STARS_INI
        = new File(System.getProperty("user.home")
                   + "/.wine/drive_c/windows/Stars.ini");

    /** The color of the serial code box. */
    private static final Color SERIAL_COLOR = new Color(212, 208, 200);

    /** X offset from center for the serial box test point. */
    private static final int SERIAL_XOFF = -82;

    /** Y offset from center for the serial box test point. */
    private static final int SERIAL_YOFF = 26;

    /** Point location to test for the serial code box. */
    private final Point serialBox;

    /** The code provider to test codes from. */
    private final CodeProvider provider;

    /**
     * Create a new tester testing the given codes.
     * @param codes  the codes provider
     * @throws AWTException from Robot
     */
    public Cracker(final CodeProvider codes) throws AWTException {
        this.provider = codes;
        Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
        serialBox = new Point(size.width / 2 + SERIAL_XOFF,
                              size.height / 2 + SERIAL_YOFF);
    }

    @Override
    public void run() {
        while (provider.hasNext()) {
            String code = provider.next();
            STARS_INI.delete();
            log.info("Trying " + code);

            /* Enter the serial code. */
            Process stars = launch();
            click(serialBox);
            type(code);
            type(KeyEvent.VK_ENTER);
            mouseMove(0, 0);
            sleep(0.25);
            if (get(serialBox).equals(SERIAL_COLOR)) {
                log.info("Invalid code");
                stars.destroy();
                continue;
            }
            stars.destroy();

            /* Make sure it stuck. */
            Process check = launch();
            click(serialBox);
            if (get(serialBox).equals(SERIAL_COLOR)) {
                log.fine("Fake code");
            } else {
                log.severe("FOUND " + code);
                provider.report(code);
            }
            check.destroy();
        }
    }

    /**
     * Launch the Stars! application.
     * @return the running process
     */
    private Process launch() {
        Runtime runtime = Runtime.getRuntime();
        try {
            Process proc = runtime.exec(STARS_COMMAND);
            sleep(0.5);
            return proc;
        } catch (Exception e) {
            log.warning(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    /**
     * Click the given point.
     * @param p  the point to click
     */
    private void click(final Point p) {
        mouseMove(p.x, p.y);
        mousePress(InputEvent.BUTTON1_MASK);
        mouseRelease(InputEvent.BUTTON1_MASK);
        sleep(0.5);
    }

    /**
     * Type the given keycode.
     * @param keycode  the code to type
     */
    private void type(final int keycode) {
        keyPress(keycode);
        keyRelease(keycode);
    }

    /**
     * Type the given keycode.
     * @param keycode  the code to type
     * @param mod      code modifier
     */
    private void type(final int keycode, final int mod) {
        keyPress(mod);
        keyPress(keycode);
        keyRelease(keycode);
        keyRelease(mod);
    }

    /**
     * Type in a string.
     * @param str  the string to type
     */
    private void type(final String str) {
        for (int i = 0; i < str.length(); i++) {
            int c = (int) str.toUpperCase().charAt(i);
            if (c > 64) {
                type(c, KeyEvent.VK_SHIFT);
            } else {
                type(c);
            }
        }
        sleep(0.25);
    }

    /**
     * Get the color at a location.
     * @param p  the location
     * @return the color
     */
    private Color get(final Point p) {
        return getPixelColor(p.x, p.y);
    }

    /**
     * Sleep for awhile.
     * @param delay  delay in seconds
     */
    private void sleep(final double delay) {
        delay((int) (delay * 1000));
    }
}
