package unstar;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Point;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Arrays;
import lombok.extern.java.Log;

/**
 * Automates the cracking process by firing up Stars! and guessing
 * codes. Assuming the host will be available to the VM (as a gateway)
 * at the address <code>REPORT_IP</code>, the host can receive
 * discovered codes by running netcat like so.
 *
 * <pre>
 * nc -uklw1 2000
 * </pre>
 */
@Log
public final class Cracker extends Robot implements Runnable {

    /** Address to report codes to (the host). */
    public static final String REPORT_IP = "10.0.2.2";

    /** Port for reporting found codes. */
    public static final int REPORT_PORT = 2000;

    /** Length of a serial code. */
    public static final int CODELEN = 8;

    /** The digits of a serial code. */
    public static final char[] DIGITS =
    {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C',
     'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P',
     'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};

    /** The command to run Stars!. */
    private static final String[] STARS_COMMAND = {"wine", "stars/stars.exe"};

    /** Location of the Stars! .ini file. */
    private static final File STARS_INI
        = new File(System.getProperty("user.home")
                   + "/.wine/drive_c/windows/Stars.ini");

    /** Point location to test for the serial code box. */
    //private static final Point SERIAL_BOX = new Point(770, 570); // Host
    private static final Point SERIAL_BOX = new Point(430, 410); // VM

    /** The color of the serial code box. */
    private static final Color SERIAL_COLOR = new Color(212, 208, 200);

    /** The prefix to use for all codes. */
    private final String prefix;

    /** The counter to increment to get codes to try. */
    private long counter;

    /** Place to write out found codes. */
    private final PrintWriter out;

    /**
     * Create a new cracker with a specific starting spot.
     * @param pre      serial code prefix for all codes
     * @param iterate  the starting point fragment
     * @throws AWTException from Robot
     * @throws IOException if there's no place to write found codes
     */
    public Cracker(final String pre, final String iterate)
        throws AWTException, IOException {
        super();
        this.prefix = pre;
        this.counter = decode(iterate) - 1;
        out = new PrintWriter("serials.log");
    }

    @Override
    public void run() {
        while (true) {
            counter++;
            STARS_INI.delete();
            String code = prefix + encode(CODELEN - prefix.length(), counter);
            log.info("Trying " + code + " (" + counter + ")");

            /* Enter the serial code. */
            Process stars = launch();
            click(SERIAL_BOX);
            type(code);
            type(KeyEvent.VK_ENTER);
            mouseMove(0, 0);
            sleep(0.25);
            if (get(SERIAL_BOX).equals(SERIAL_COLOR)) {
                log.info("Invalid code");
                stars.destroy();
                continue;
            }
            stars.destroy();

            /* Make sure it stuck. */
            Process check = launch();
            click(SERIAL_BOX);
            if (get(SERIAL_BOX).equals(SERIAL_COLOR)) {
                log.info("Fake code");
            } else {
                log.severe("FOUND " + code);
                out.println(code);
                out.flush();
                try {
                    byte[] msg = (code + "\n").getBytes();
                    InetAddress dest = InetAddress.getByName(REPORT_IP);
                    DatagramPacket packet =
                        new DatagramPacket(msg, msg.length, dest, REPORT_PORT);
                    new DatagramSocket().send(packet);
                } catch (Exception e) {
                    log.warning("Unable to report serial code.");
                }
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

    /**
     * Encode the given number into a serial code (base 36).
     * @param len  the length of the code
     * @param val  the number to encode
     * @return the serial code
     */
    public static String encode(final long len, final long val) {
        StringBuilder str = new StringBuilder();
        long div = val;
        while (div > 0) {
            int digit = (int) (div % DIGITS.length);
            str.append(DIGITS[digit]);
            div = div / DIGITS.length;
        }
        while (str.length() < len) {
            str.append(DIGITS[0]);
        }
        return str.reverse().toString();
    }

    /**
     * Decode a serial code longo a number.
     * @param value  the serial code
     * @return the number for this code
     */
    public static long decode(final String value) {
        long total = 0;
        for (int i = 0; i < value.length(); i++) {
            char c = value.toUpperCase().charAt(i);
            total = total * DIGITS.length + Arrays.binarySearch(DIGITS, c);
        }
        return total;
    }
}
