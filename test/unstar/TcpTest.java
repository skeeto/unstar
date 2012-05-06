package unstar;

import java.io.InputStream;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.NoSuchElementException;
import java.util.Scanner;
import lombok.SneakyThrows;
import org.junit.Test;
import static org.junit.Assert.*;

public class TcpTest {

    private static final int PORT = 2000;
    private static final String[] codes = {"FOO0BAR1", "ZZBAZFOO", "0000FFFF"};

    @Test(timeout=4000)
    @SneakyThrows
    public void tcpTest() {
        /* Run the server in a thread. */
        TestServer test = new TestServer();
        Thread server = new Thread(test);
        server.start();

        /* Receive each code. */
        CodeProvider tcp = new TcpCodeProvider("localhost", PORT);
        for (String code : codes) {
            assertEquals(code, tcp.next());
        }
        tcp.report(codes[1]);
        assertFalse(tcp.hasNext());
        boolean thrown = false;
        try {
            tcp.next();
        } catch (NoSuchElementException e) {
            thrown = true;
        }
        assertTrue(thrown);

        /* Check the report. */
        server.join();
        assertEquals(codes[1], test.result);
    }

    private static class TestServer implements Runnable {

        private final ServerSocket server;

        /** Used to force the other thread to wait until this one completes. */
        private String result;

        @SneakyThrows
        public TestServer() {
            server = new ServerSocket(PORT);
        }

        @Override
        @SneakyThrows
        public void run() {
            Socket client = server.accept();
            PrintStream out = new PrintStream(client.getOutputStream(), true);
            Scanner in = new Scanner(client.getInputStream());
            for (String code : codes) {
                assertEquals(in.nextLine(), "*next");
                out.println(code);
            }
            out.println(InputStreamCodeProvider.TERMINATOR);
            out.flush();
            result = in.nextLine();
        }
    }
}
