package jp.vmi.selenium.selenese;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.URL;
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
            Assume.assumeNoException(e);
        } catch (IOException e) {
            Assume.assumeNoException(e);
        }
    }

    public static File getScriptFile(Class<?> clazz, String name) {
        String html = "/" + clazz.getCanonicalName().replace('.', '/') + name + ".html";
        URL resource = clazz.getResource(html);
        if (resource == null)
            throw new RuntimeException(new FileNotFoundException(html));
        try {
            return new File(resource.toURI());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

}
