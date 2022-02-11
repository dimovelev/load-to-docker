package com.prime157.docker.load.maven;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.prime157.docker.load.common.LoadToDocker;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

@Mojo(name = "load-to-docker")
public class LoadToDockerMojo extends AbstractMojo {
    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    private MavenProject project;
    /**
     * The path to the tarball to load to docker
     */
    @Parameter(property = "load-to-docker.tarball", defaultValue = "target/jib-image.tar")
    private String tarball;
    /**
     * The path to the tarball to load to docker
     */
    @Parameter(property = "load-to-docker.dockerUri")
    private String dockerUri;
    /**
     * Whether to fail if the path does not exist.
     */
    @Parameter(property = "load-to-docker.fail-on-missing-tarball", defaultValue = "true")
    private boolean failOnMissingTarball = true;

    @Override
    public void execute() throws MojoExecutionException {
        final Path input = Paths.get(tarball);
        if (!Files.exists(input)) {
            if (failOnMissingTarball) {
                throw new MojoExecutionException("Could not find tarball " + input);
            } else {
                getLog().warn("Skipping because the file \"" + input + "\" does not exist");
            }
        }

        final LoadToDocker loader = new LoadToDocker(input,
            dockerUri == null || dockerUri.isEmpty() ? null : URI.create(dockerUri), getLog()::info);
        try {
            loader.load();
        } catch (final IOException | RuntimeException e) {
            throw new MojoExecutionException("Failed to load to docker", e);
        }
    }

}