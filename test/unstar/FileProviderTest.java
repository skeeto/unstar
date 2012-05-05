package unstar;

import java.io.File;
import java.io.PrintStream;
import java.util.NoSuchElementException;
import lombok.SneakyThrows;
import org.junit.Test;
import static org.junit.Assert.*;

public class FileProviderTest {

    private String[] codes = {"ALPHA000", "BETA0000", "FOOFFFFF"};

    @Test
    @SneakyThrows
    public void fileTest() {
        File file = File.createTempFile("codes", null);
        file.deleteOnExit();
        PrintStream out = new PrintStream(file);
        for (String code : codes) {
            out.println(code);
        }
        CodeProvider provider = new FileCodeProvider(file);
        for (String code : codes) {
            assertEquals(code, provider.next());
        }
        assertFalse(provider.hasNext());
        boolean thrown = false;
        try {
            provider.next();
        } catch (NoSuchElementException e) {
            thrown = true;
        }
        assertTrue(thrown);
    }
}
