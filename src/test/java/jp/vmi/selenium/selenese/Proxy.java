package jp.vmi.selenium.selenese;

import java.net.ServerSocket;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.commons.lang.math.RandomUtils;
import org.jruby.embed.LocalVariableBehavior;
import org.jruby.embed.ScriptingContainer;

@SuppressWarnings({ "rawtypes", "unused" })
public class Proxy {

    public static int PORTNUM_MAX = 65535;
    public static int PORTNUM_MIN = 10000;

    private final ScriptingContainer container;

    private int port = 18080;

    private final ExecutorService executor = Executors.newFixedThreadPool(2);

    private Future start;

    public Proxy() {
        super();
        port = getUsablePort();
        container = new ScriptingContainer(LocalVariableBehavior.PERSISTENT);
        container.setError(System.err);
        container.setOutput(System.out);
        container.put("port", port);
        container.runScriptlet("require 'webrick'");
        container.runScriptlet("require 'webrick/httpproxy'");
        container.runScriptlet("server = WEBrick::HTTPProxyServer.new({:Port => port})");
    }

    public int getPort() {
        return port;
    }

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

    public void start() {
        start = executor.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    container.runScriptlet("server.start");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        Future waitStart = executor.submit(new Runnable() {
            @Override
            public void run() {
                boolean running = false;
                while (!running) {
                    running = (Boolean) container.runScriptlet("server.status == :Running");
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });
        try {
            waitStart.get();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    public void stop() {
        Future stop = executor.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    container.runScriptlet("server.shutdown");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        Future waitStop = executor.submit(new Runnable() {
            @Override
            public void run() {
                boolean stop = false;
                while (!stop) {
                    stop = (Boolean) container.runScriptlet("server.status == :Stop");
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });
        try {
            stop.get();
            waitStop.get();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    public void kill() {
        container.terminate();
    }
}
