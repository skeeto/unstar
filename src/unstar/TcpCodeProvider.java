package unstar;

import java.io.PrintStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import lombok.SneakyThrows;

/**
 * Just like the InputStreamCodeProvider, but with a remote host. All
 * reports go back out to the host.
 */
public final class TcpCodeProvider implements CodeProvider {

    /** The delegate input stream provider. */
    private final CodeProvider delegate;

    /** The reporting stream. */
    private final PrintStream out;

    /** True if a request for another code has been sent but not yet read. */
    private boolean requested = false;

    /**
     * Create a new provider connection to a remote host.
     * @param host  the remote host
     * @param port  the remote port
     * @throws UnknownHostException if the host cannot be found
     */
    @SneakyThrows
    public TcpCodeProvider(final String host, final int port)
        throws UnknownHostException {
        Socket remote = new Socket(InetAddress.getByName(host), port);
        delegate = new InputStreamCodeProvider(remote.getInputStream());
        out = new PrintStream(remote.getOutputStream(), true);
    }

    @Override
    public String next() {
        if (!requested) {
            out.println("*next");
        }
        requested = false;
        return delegate.next();
    }

    @Override
    public boolean hasNext() {
        if (!requested) {
            out.println("*next");
            requested = true;
        }
        return delegate.hasNext();
    }

    @Override
    public void report(final String code) {
        out.println(code);
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
}
