package unstar;

import org.junit.Test;
import static org.junit.Assert.*;

public class CrackerTest {

    @Test
    public void digitTest() {
        assertEquals(36, Cracker.DIGITS.length);
    }

    @Test
    public void encodeTest() {
        assertEquals("1", Cracker.encode(1, 1));
        assertEquals("A", Cracker.encode(1, 10));
        assertEquals("00RS", Cracker.encode(4, 1000));
        assertEquals(1000, Cracker.decode("00RS"));
    }
}
