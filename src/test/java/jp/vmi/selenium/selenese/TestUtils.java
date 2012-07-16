package jp.vmi.selenium.selenese;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import org.junit.Assume;

public final class TestUtils {

    private TestUtils() {
        // no operation
    }

    public static void checkProxy() {
        // assume that local proxy is available.
        try {
            Socket sock = new Socket("127.0.0.1", 18080);
            sock.close();
        } catch (UnknownHostException e) {
            Assume.assumeTrue(false);
        } catch (IOException e) {
            Assume.assumeTrue(false);
        }
    }

}
