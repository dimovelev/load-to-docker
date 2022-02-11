package com.prime157.docker.load.common;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;
import java.util.function.Consumer;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.FileEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;

public class LoadToDocker {
    private final Path tarball;
    private final URI dockerUri;
    private final Consumer<String> logger;

    public LoadToDocker(final Path tarball, final URI dockerUri, final Consumer<String> logger) {
        this.tarball = tarball;
        this.dockerUri = dockerUri;
        this.logger = logger;
    }

    public void load() throws IOException {
        try (final CloseableHttpClient client = createClient()) {
            final HttpPost req = new HttpPost("http://localhost/images/load");
            req.setHeader("Accept", "application/json");
            req.setEntity(new FileEntity(tarball.toFile(), ContentType.create("application/x-tar")));
            try (final CloseableHttpResponse res = client.execute(req)) {
                if (res.getStatusLine().getStatusCode() == 200) {
                    logger.accept("Loaded to docker: " + EntityUtils.toString(res.getEntity()));
                } else {
                    throw new IllegalStateException("Failed to load tarball " + tarball + ": " + res.getStatusLine());
                }
            }
        }

    }

    private CloseableHttpClient createClient() {
        if (dockerUri == null) {
            return HttpClientUtil.createClient();
        } else {
            return HttpClientUtil.createClient(dockerUri);
        }
    }

}
