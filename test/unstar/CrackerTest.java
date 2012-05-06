package unstar;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Random;
import lombok.SneakyThrows;
import org.junit.Test;
import static org.junit.Assert.*;

public class CrackerTest {

    private static final int TESTLEN = 1024 * 1024;

    @Test
    @SneakyThrows
    public void copyTest() {
        Random rng = new Random(0);
        byte[] msgin = new byte[TESTLEN];
        ByteArrayOutputStream out = new ByteArrayOutputStream(TESTLEN);
        rng.nextBytes(msgin);
        Cracker.copy(new ByteArrayInputStream(msgin), out);
        assertArrayEquals(msgin, out.toByteArray());
    }
}
