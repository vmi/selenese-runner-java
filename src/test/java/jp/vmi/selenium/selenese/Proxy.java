package jp.vmi.selenium.selenese;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.commons.lang.math.RandomUtils;
import org.jruby.embed.LocalVariableBehavior;
import org.jruby.embed.ScriptingContainer;

public class Proxy {
    private ScriptingContainer container;

    private int port = 18080;

    private ExecutorService executor = Executors.newFixedThreadPool(2);

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
        return RandomUtils.nextInt(65535 - 1024) + 1024;
    }

    public void start() {
        Future start = executor.submit(new Runnable() {
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
                    System.out.println(running);
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
                    System.out.println(stop);
                }
            }
        });
        try {
            waitStop.get();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }
}
