package jp.vmi.selenium.testutils.proxy;

import java.util.Objects;

import org.littleshoot.proxy.ProxyAuthenticator;

/**
 * Proxy basic authenticator.
 */
public class ProxyBasicAuhthenticator implements ProxyAuthenticator {

    private final String userName;
    private final String password;

    /**
     * Constructor.
     *
     * @param userName proxy user name.
     * @param password proxy password.
     */
    public ProxyBasicAuhthenticator(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }

    @Override
    public String getRealm() {
        return "SRJ Proxy Server";
    }

    @Override
    public boolean authenticate(String userName, String password) {
        return Objects.equals(this.userName, userName)
            && Objects.equals(this.password, password);
    }
}
