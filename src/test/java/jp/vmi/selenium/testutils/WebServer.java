package jp.vmi.selenium.testutils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.apache.commons.text.StringSubstitutor;
import org.openqa.selenium.net.PortProber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

/**
 * Webserver for unit test.
 */
@SuppressWarnings("restriction")
public class WebServer {

    private static final Logger log = LoggerFactory.getLogger(WebServer.class);

    private static String urlDecode(String s) {
        try {
            return URLDecoder.decode(s, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return "";
        }
    }

    private static void parseQuery(Map<String, String> params, String queryString) {
        if (Strings.isNullOrEmpty(queryString))
            return;
        for (String entryString : queryString.split("&")) {
            String[] pair = entryString.split("=", 2);
            String key = urlDecode(pair[0]);
            String value = (pair.length == 2) ? urlDecode(pair[1]) : "";
            params.put(key, value);
        }
    }

    private static String getContentType(Path path) {
        String ext = FilenameUtils.getExtension(path.toString()).toLowerCase();
        switch (ext) {
        case "html":
            return Content.TEXT_HTML;
        case "js":
            return "application/javascript";
        case "jpg":
            ext = "jpeg";
            // fall through
        case "jpeg":
        case "png":
            return "image/" + ext;
        case "ico":
            return "image/vnd.microsoft.icon";
        default:
            throw new UnsupportedOperationException(path.toString());
        }
    }

    private final int port;

    private final String htdocs;

    private String fqdn = "localhost";

    private HttpServer server = null;

    private Thread shutdownHook = null;

    /**
     * Constructor.
     *
     * This use free port.
     */
    public WebServer() {
        this(PortProber.findFreePort(), null);
    }

    /**
     * Constructor.
     *
     * @param port port.
     * @param htdocs document root.
     */
    public WebServer(int port, String htdocs) {
        this.port = port;
        if (htdocs == null)
            htdocs = WebServer.class.getResource("/htdocs").getFile();
        // convert to correct local path (required for windows)
        this.htdocs = new File(htdocs).getPath();
    }

    private static class HttpErrorException extends Exception {

        private static final long serialVersionUID = 1L;

        public final Status status;

        public HttpErrorException(Status status) {
            this.status = status;
        }
    }

    private static enum Status {
        // OK
        OK(200, "OK"),
        // not found.
        NOT_FOUND(403, "Not Found"),
        // forbidden
        FORBIDDEN(404, "Forbidden"),
        // internal server error.
        INTERNAL_SERVER_ERROR(500, "Internal Server Error"),
        ;
        private final int code;
        private final String message;

        private Status(int status, String message) {
            this.code = status;
            this.message = message;
        }

        @Override
        public String toString() {
            return code + " " + message;
        }
    }

    private static class Content {
        public static final String TEXT_HTML = "text/html; charset=UTF-8";

        public Status status;
        public String type;
        public byte[] body;

        public Content(Status status, String message) {
            this.status = status;
            this.type = TEXT_HTML;
            this.body = message.getBytes(StandardCharsets.UTF_8);
        }

        public Content(String type) {
            this.status = Status.OK;
            this.type = type;
        }
    }

    private static class SimpleTemplateHandler implements HttpHandler {

        private static final String WAIT = "wait";

        private final String htdocs;

        public SimpleTemplateHandler(String htdocs) {
            this.htdocs = htdocs;
        }

        private static final Pattern TITLE_RE = Pattern.compile("<title>(.*)</title>", Pattern.CASE_INSENSITIVE);

        private static String getTitle(Path path) {
            try (BufferedReader r = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
                String line;
                while ((line = r.readLine()) != null) {
                    Matcher matcher = TITLE_RE.matcher(line);
                    if (matcher.find())
                        return matcher.group(1);
                }
                return path.getFileName().toString();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        private static Content handleIndex(Path path, final Map<String, String> params) throws HttpErrorException {
            try {
                TreeMap<String, String> entries = new TreeMap<>();
                Files.newDirectoryStream(path).forEach(entry -> {
                    String fileName = entry.getFileName().toString();
                    String link;
                    if (Files.isRegularFile(entry)) {
                        if (fileName.equals("index.html") || fileName.equals("template.html") || !fileName.endsWith(".html"))
                            return;
                        link = "<li><a href=\"" + fileName + "\">" + getTitle(entry) + "</a></li>";
                    } else if (Files.isDirectory(entry)) {
                        link = "<li><a href=\"" + fileName + "/\">" + fileName + "/</a></li>";
                    } else {
                        return;
                    }
                    entries.put(fileName, link);
                });
                Content content = new Content(Content.TEXT_HTML);
                String tmpl = IOUtils.toString(Files.newInputStream(path.resolve("index.html")), StandardCharsets.UTF_8);
                content.body = new StringSubstitutor(key -> {
                    switch (key) {
                    case "entries":
                        return entries.values().stream().collect(Collectors.joining("\n"));
                    default:
                        return "";
                    }
                }).replace(tmpl).getBytes(StandardCharsets.UTF_8);
                return content;
            } catch (Exception e) {
                throw new HttpErrorException(Status.INTERNAL_SERVER_ERROR);
            }
        }

        private static Content handleContent(Path path, final Map<String, String> params) throws HttpErrorException {
            try {
                Content content = new Content(getContentType(path));
                if (content.type.startsWith("text/html")) {
                    String tmpl = IOUtils.toString(Files.newInputStream(path), StandardCharsets.UTF_8);
                    content.body = new StringSubstitutor(key -> {
                        String value = params.get(key);
                        return value != null ? StringEscapeUtils.escapeHtml4(value) : "";
                    }).replace(tmpl).getBytes(StandardCharsets.UTF_8);
                } else {
                    content.body = Files.readAllBytes(path);
                }
                if (params.containsKey(WAIT)) {
                    int wait = NumberUtils.toInt(params.get(WAIT));
                    if (wait > 0) {
                        try {
                            log.debug("[{}] Waiting {} sec.", Thread.currentThread().getName(), wait);
                            Thread.sleep(wait * 1000);
                        } catch (InterruptedException e) {
                            // no operation.
                        }
                    }
                }
                return content;
            } catch (FileNotFoundException e) {
                throw new HttpErrorException(Status.NOT_FOUND);
            } catch (IOException e) {
                throw new HttpErrorException(Status.INTERNAL_SERVER_ERROR);
            }
        }

        private static Content dirList(Path path, String name) {
            String escName = StringEscapeUtils.escapeHtml4(name);
            StringBuilder html = new StringBuilder("<!DOCTYPE html>"
                + "<html>"
                + "<head>"
                + "<title>")
                    .append(escName)
                    .append("</title>"
                        + "</head>"
                        + "<body>"
                        + "<h1>")
                    .append(escName)
                    .append("</h1>"
                        + "<hr>"
                        + "<ul>");
            String[] filenames = path.toFile().list();
            for (String filename : filenames) {
                String escFilename = StringEscapeUtils.escapeHtml4(filename);
                html.append("<li><a href=\"").append(escFilename).append("\">")
                    .append(escFilename)
                    .append("</a></li>");
            }
            html.append("<hr>"
                + "</body>"
                + "</html>");
            return new Content(Status.OK, html.toString());
        }

        private Content handlePath(HttpExchange he) throws HttpErrorException {
            URI uri = he.getRequestURI();
            String uriPath = uri.getPath();
            if (uriPath.matches("\\\\|//|/\\.|\\.\\."))
                throw new HttpErrorException(Status.FORBIDDEN);
            Map<String, String> params = new HashMap<>();
            try {
                String postData = IOUtils.toString(he.getRequestBody(), StandardCharsets.UTF_8);
                parseQuery(params, postData);
            } catch (IOException e) {
                // no operation.
            }
            parseQuery(params, uri.getRawQuery());
            if (uriPath.equals("/")) {
                return handleIndex(Paths.get(htdocs), params);
            } else {
                Path path = Paths.get(htdocs, uriPath);
                if (Files.isRegularFile(path)) {
                    return handleContent(path, params);
                } else if (Files.isDirectory(path)) {
                    Path index = path.resolve("index.html");
                    if (Files.isRegularFile(index))
                        return handleContent(index, params);
                    else
                        return dirList(path, uri.getPath());
                } else {
                    throw new HttpErrorException(Status.NOT_FOUND);
                }
            }
        }

        private Content handleError(Status status) {
            return new Content(status,
                "<!DOCTYPE html><html><head><title>" + status + "</title></head>"
                    + "<body>" + status + "</body></html>");
        }

        @Override
        public void handle(HttpExchange he) throws IOException {
            Content content;
            if (he.getRemoteAddress().getAddress().isLoopbackAddress()) {
                try {
                    content = handlePath(he);
                } catch (HttpErrorException e) {
                    content = handleError(e.status);
                }
            } else {
                content = handleError(Status.FORBIDDEN);
            }
            Headers headers = he.getResponseHeaders();
            headers.put("Content-Type", Arrays.asList(content.type));
            he.sendResponseHeaders(content.status.code, content.body.length);
            try (OutputStream os = he.getResponseBody()) {
                os.write(content.body);
            }
            log.debug("[{}] {} {} [{}] {}", Thread.currentThread().getName(), he.getRequestMethod(), he.getRequestURI(), content.status, content.type);
        }
    }

    /**
     * Start web server.
     */
    public synchronized void start() {
        InetSocketAddress sock = new InetSocketAddress(fqdn, port);
        try {
            server = HttpServer.create(sock, 0);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        server.setExecutor(Executors.newCachedThreadPool());
        server.createContext("/", new SimpleTemplateHandler(htdocs));
        //server.createContext("/basic", new BasicAuthenticationHandler(new InMemoryPasswords().add("user", "pass")));
        //server.createContext("/redirect", new RedirectHandler("http://" + getServerNameString() + "/index.html"));
        //server.createContext("/basic/redirect", new RedirectHandler("http://" + getServerNameString() + "/basic/index.html"));
        shutdownHook = new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (WebServer.this) {
                    shutdownHook = null;
                    if (server != null) {
                        System.err.println();
                        stop();
                    }
                }
            }
        });
        Runtime.getRuntime().addShutdownHook(shutdownHook);
        server.start();
        log.info("Started.");
    }

    /**
     * Stop web server.
     */
    public synchronized void stop() {
        if (shutdownHook != null) {
            Runtime.getRuntime().removeShutdownHook(shutdownHook);
            shutdownHook = null;
        }
        if (server != null) {
            server.stop(0);
            server = null;
            log.info("Stopped.");
        } else {
            log.info("Already stopped.");
        }
    }

    /**
     * Get server port.
     *
     * @return port number.
     */
    public int getServerPort() {
        return port;
    }

    /**
     * Get server name.
     *
     * @return server name.
     */
    public String getServerNameString() {
        return fqdn + ":" + port;
    }

    /**
     * Get base URL.
     *
     * @return base URL.
     */
    public String getBaseURL() {
        return "http://" + getServerNameString() + "/";
    }

    /**
     * Set FQDN of base URL for android test.
     *
     * @param fqdn FQDN.
     */
    public void setFqdn(String fqdn) {
        this.fqdn = fqdn;
    }

    /**
     * Start test web server.
     *
     * @param args ignored.
     */
    public static void main(String[] args) {
        int port = 8080;
        String htdocs = null;
        switch (args.length) {
        case 2:
            htdocs = args[1];
            // fall through
        case 1:
            port = Integer.parseInt(args[0]);
            // fall through
        case 0:
            break;
        default:
            throw new IllegalArgumentException(String.join(" ", args));
        }
        new WebServer(port, htdocs).start();
    }
}
