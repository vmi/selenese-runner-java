package jp.vmi.selenium.testutils;

import org.webbitserver.HttpControl;
import org.webbitserver.HttpHandler;
import org.webbitserver.HttpRequest;
import org.webbitserver.HttpResponse;

/**
 * Http Redirect handler for webbit
 * @author hayato
 */
public class RedirectHandler implements HttpHandler {

    String location = "";

    /**
     * constructor.
     * @param location location for redirect.
     */
    public RedirectHandler(String location) {
        this.location = location;
    }

    @Override
    public void handleHttpRequest(HttpRequest request, HttpResponse response, HttpControl control) throws Exception {
        response.status(303).header("location", location).end();
    }

}
