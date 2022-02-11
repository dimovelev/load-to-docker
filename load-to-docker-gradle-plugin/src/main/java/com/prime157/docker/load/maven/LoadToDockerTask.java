package com.prime157.docker.load.maven;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.inject.Inject;

import com.prime157.docker.load.common.LoadToDocker;
import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;

public class LoadToDockerTask extends DefaultTask {
    private final LoadToDockerExtension extension;

    @Inject
    public LoadToDockerTask(final LoadToDockerExtension extension) {
        this.extension = extension;
    }

    @TaskAction
    public void loadToDocker() throws IOException {
        if (extension.getTarball() == null) {
            throw new IllegalStateException("Tarball not specified");
        }
        final Path tarball = Paths.get(extension.getTarball());
        final Path path;
        if (tarball.isAbsolute()) {
            path = tarball;
        } else {
            path = getProject().getProjectDir().toPath().resolve(tarball);
        }
        if (!Files.exists(path)) {
            throw new IllegalStateException(path.toAbsolutePath() + " not found");
        }

        final LoadToDocker loader = new LoadToDocker(tarball, null, msg -> {
            getLogger().info(msg);
        });

        loader.load();
    }
}
