package com.prime157.docker.load.common;

import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

public class HttpClientUtil {
    private HttpClientUtil() {
        // private constructor to prevent instantiation of utility class
    }

    public static CloseableHttpClient createClient() {
        if (Files.exists(Paths.get("/var/run/docker.sock"))) {
            return createClient(URI.create("unix:///var/run/docker.sock"));
        }
        if (Files.exists(Paths.get("\\\\.\\pipe\\docker_engine"))) {
            return createClient(URI.create("npipe:////./pipe/docker_engine"));
        }
        throw new IllegalStateException("Could not determine docker endpoint");
    }

    public static CloseableHttpClient createClient(final URI uri) {
        final Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create() //
            .register("http", createConnectionFactory(uri)) //
            .build();

        final PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(registry);
        connectionManager.setMaxTotal(10);
        connectionManager.setDefaultMaxPerRoute(10);

        final CloseableHttpClient result = HttpClients.custom() //
            .setConnectionManager(connectionManager) //
            .disableConnectionState() //
            .build();

        return result;
    }

    public static ConnectionSocketFactory createConnectionFactory(final URI uri) {
        switch (uri.getScheme()) {
            case "npipe":
                return new NamedPipeConnectionSocketFactory(Paths.get(uri.getPath().replace('/', '\\')));
            case "unix":
                return new UnixDomainConnectionSocketFactory(Paths.get(uri.getPath()));
            default:
                throw new IllegalArgumentException("Unsupported URI " + uri);
        }
    }
}
