package jp.vmi.selenium.selenese;

import java.io.File;

import org.jruby.embed.LocalVariableBehavior;
import org.jruby.embed.ScriptingContainer;

/**
 * Webserver for unit test.
 * TODO: merge to Proxy.java
 */
public class WebServer extends WebrickServer {

    /**
     * constructor.
     */
    public WebServer() {
        super();
    }

    @Override
    protected ScriptingContainer createScriptingContainer() {
        port = NetUtils.getUsablePort();

        ScriptingContainer container = new ScriptingContainer(LocalVariableBehavior.PERSISTENT);
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
        container
            .runScriptlet("server.mount_proc('/redirect'){|req,res| res.set_redirect(WEBrick::HTTPStatus[301],'http://"
                + this.getServerNameString() + "/index.html')}");
        return container;
    }
}
