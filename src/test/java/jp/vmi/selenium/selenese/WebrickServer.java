package jp.vmi.selenium.selenese;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.jruby.embed.ScriptingContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * WEBrick server class
 * 
 * @author hayato
 */
@SuppressWarnings({ "rawtypes", "unused" })
public abstract class WebrickServer {

    private static final Logger log = LoggerFactory.getLogger(Proxy.class);
    protected final ScriptingContainer container;
    protected int port;
    private final ExecutorService executor = Executors.newFixedThreadPool(2);
    private Future start;

    /**
     * constructor.
     */
    public WebrickServer() {
        super();
        container = createScriptingContainer();
    }

    /**
     * create ScriptingContainer
     * 
     * @return new ScriptingContainer instance include webrick instance 
     */
    protected abstract ScriptingContainer createScriptingContainer();

    /**
     * Get server string as "HOST:PORT".
     *
     * @return server string.
     */
    public String getServerNameString() {
        return "localhost:" + port;
    }

    /**
     * Get port number of server.
     *
     * @return port number.
     */
    public int getPort() {
        return port;
    }

    /**
     * Start server.
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
     * Stop server.
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
     * Kill server.
     */
    public void kill() {
        log.info("killing webrick server...");
        container.terminate();
        log.info("killed webrick server.");
    }

}
