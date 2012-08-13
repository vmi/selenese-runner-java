package jp.vmi.selenium.selenese;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.jruby.embed.LocalVariableBehavior;
import org.jruby.embed.ScriptingContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Proxy for unit test.
 */
@SuppressWarnings({ "rawtypes", "unused" })
public class Proxy extends WebrickServer {

    private static final Logger log = LoggerFactory.getLogger(Proxy.class);

    private final ScriptingContainer container;

    private int port;

    private final ExecutorService executor = Executors.newFixedThreadPool(2);

    private Future start;

    /**
     * Constructor.
     */
    public Proxy() {
        super();
        container = createScriptingContainer();
    }

    @Override
    protected ScriptingContainer createScriptingContainer() {
        port = NetUtils.getUsablePort();
        ScriptingContainer container = new ScriptingContainer(LocalVariableBehavior.PERSISTENT);
        container.setError(System.err);
        container.setOutput(System.out);
        container.put("port", port);
        container.runScriptlet("require 'webrick'");
        container.runScriptlet("require 'webrick/httpproxy'");
        container.runScriptlet("server = WEBrick::HTTPProxyServer.new({:Port => port})");
        return container;
    }

    /**
     * Get proxy string as "HOST:PORT".
     *
     * @return proxy string.
     */
    public String getServerNameString() {
        return "localhost:" + port;
    }

    /**
     * Get port number of proxy server.
     *
     * @return port number.
     */
    public int getPort() {
        return port;
    }

    /**
     * Start proxy server.
     */
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

    /**
     * Stop proxy server.
     */
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

    /**
     * Kill proxy server.
     */
    public void kill() {
        log.info("killing proxy...");
        container.terminate();
        log.info("killed proxy.");
    }
}
