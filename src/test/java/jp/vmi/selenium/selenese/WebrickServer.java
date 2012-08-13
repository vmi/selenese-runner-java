package jp.vmi.selenium.selenese;

import org.jruby.embed.ScriptingContainer;

/**
 * WEBrick server class
 * 
 * @author hayato
 */
public abstract class WebrickServer {

    /**
     * create ScriptingContainer
     * 
     * @return new ScriptingContainer instance include webrick instance 
     */
    protected abstract ScriptingContainer createScriptingContainer();

}
