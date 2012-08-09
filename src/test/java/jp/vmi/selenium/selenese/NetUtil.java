package jp.vmi.selenium.selenese;

import java.net.ServerSocket;

import org.apache.commons.lang.math.RandomUtils;

public class NetUtil {
    protected static int PORTNUM_MAX = 65535;
    protected static int PORTNUM_MIN = 10000;

    protected static int getUsablePort() {
        int port;
        do {
            port = RandomUtils.nextInt(PORTNUM_MAX - PORTNUM_MIN) + PORTNUM_MIN;
        } while (!canUse(port));
        return port;
    }

    static boolean canUse(int port) {
        ServerSocket socket = null;
        try {
            socket = new ServerSocket(port);
            //portが開けた
            return true;
        } catch (java.net.BindException e) {
            // port already used
            return false;
        } catch (java.io.IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (Exception e) {
                }
            }
        }
    }
}
