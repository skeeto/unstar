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
import java.rmi.UnknownHostException;
import java.util.Arrays;
import lombok.extern.java.Log;

@Log
public class Cracker extends Robot implements Runnable {

    public static final String REPORT_IP = "10.0.2.2";
    public static final int REPORT_PORT = 2000;

    public static final int CODELEN = 8;

    public static final char[] DIGITS =
    {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C',
     'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P',
     'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};

    private static final String[] STARS_COMMAND = {"wine", "stars/stars.exe"};

    private static final File STARS_INI
        = new File(System.getProperty("user.home")
                   + "/.wine/drive_c/windows/Stars.ini");

    //private static final Point SERIAL_BOX = new Point(770, 570); // Host
    private static final Point SERIAL_BOX = new Point(430, 410); // VM
    private static final Color SERIAL_COLOR = new Color(212, 208, 200);

    private final String prefix;
    private int counter;
    private final PrintWriter out;

    public Cracker(String prefix, String iterate)
        throws AWTException, IOException {
        super();
        this.prefix = prefix;
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
            sleep(1.0);
            click(SERIAL_BOX);
            type(code);
            type(KeyEvent.VK_ENTER);
            mouseMove(0, 0);
            sleep(1.0);
            if (get(SERIAL_BOX).equals(SERIAL_COLOR)) {
                log.info("Invalid code");
                stars.destroy();
                continue;
            }
            stars.destroy();

            /* Make sure it stuck. */
            Process check = launch();
            sleep(1.0);
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
            return proc;
        } catch (Exception e) {
            log.warning(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private void click(Point p) {
        mouseMove(p.x, p.y);
        mousePress(InputEvent.BUTTON1_MASK);
        mouseRelease(InputEvent.BUTTON1_MASK);
        sleep(0.5);
    }

    private void type(int keycode) {
        keyPress(keycode);
        keyRelease(keycode);
    }

    private void type(int keycode, int mod) {
        keyPress(mod);
        keyPress(keycode);
        keyRelease(keycode);
        keyRelease(mod);
    }

    private void type(String str) {
        for (int i = 0; i < str.length(); i++) {
            int c = (int) str.toUpperCase().charAt(i);
            if (c > 64)
                type(c, KeyEvent.VK_SHIFT);
            else
                type(c);
        }
        sleep(0.25);
    }

    private Color get(Point p) {
        return getPixelColor(p.x, p.y);
    }

    private void sleep(double delay) {
        delay((int) (delay * 1000));
    }

    public static String encode(int len, int val) {
        StringBuilder str = new StringBuilder();
        int div = val;
        while (div > 0) {
            int digit = div % DIGITS.length;
            str.append(DIGITS[digit]);
            div = div / DIGITS.length;
        }
        while (str.length() < len) {
            str.append(DIGITS[0]);
        }
        return str.reverse().toString();
    }

    public static int decode(String value) {
        int total = 0;
        for (int i = 0; i < value.length(); i++) {
            char c =value.toUpperCase().charAt(i);
            total = total * DIGITS.length + Arrays.binarySearch(DIGITS, c);
        }
        return total;
    }
}
