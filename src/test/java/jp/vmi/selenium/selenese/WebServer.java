package jp.vmi.selenium.selenese;

import java.io.File;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.jruby.embed.LocalVariableBehavior;
import org.jruby.embed.ScriptingContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Webserver for unit test.
 * TODO: merge to Proxy.java
 */
@SuppressWarnings({ "rawtypes", "unused" })
public class WebServer extends WebrickServer {

    private static final Logger log = LoggerFactory.getLogger(WebServer.class);

    private final ScriptingContainer container;

    private final int port;

    private final ExecutorService executor = Executors.newFixedThreadPool(2);

    private Future start;

    public WebServer() {
        super();
        port = NetUtils.getUsablePort();
        container = new ScriptingContainer(LocalVariableBehavior.PERSISTENT);
        container.setError(System.err);
        container.setOutput(System.out);
        container.put("port", port);
        File classpath = new File(this.getClass().getResource("").getPath());
        File documentroot = new File(classpath, "htdocs");
        container.put("documentroot", documentroot.getAbsolutePath());
        container.put("user", "user");
        container.put("pass", "pass");
        container.runScriptlet("require 'webrick'");
        container.runScriptlet("include WEBrick");

        container
            .runScriptlet("server = WEBrick::HTTPServer.new({:Port => port, :DocumentRoot => documentroot, :RequestCallback => lambda {|req,res| HTTPAuth.basic_auth(req,res,'realm') {|u,p| u==user && p==pass }} })");
    }

    public String getServerNameString() {
        return "localhost:" + port;
    }

    public int getPort() {
        return port;
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
        log.info("killing webserver...");
        container.terminate();
        log.info("killed webserver.");
    }
}
