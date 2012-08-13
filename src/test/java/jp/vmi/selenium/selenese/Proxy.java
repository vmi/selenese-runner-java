package jp.vmi.selenium.selenese;

import org.jruby.embed.LocalVariableBehavior;
import org.jruby.embed.ScriptingContainer;

/**
 * Proxy for unit test.
 */
public class Proxy extends WebrickServer {

    /**
     * Constructor.
     */
    public Proxy() {
        super();
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
}
