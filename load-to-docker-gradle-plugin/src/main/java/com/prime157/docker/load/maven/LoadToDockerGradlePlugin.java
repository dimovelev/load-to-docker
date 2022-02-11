package com.prime157.docker.load.maven;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class LoadToDockerGradlePlugin implements Plugin<Project> {
    @Override
    public void apply(final Project project) {
        final LoadToDockerExtension extension = project.getExtensions().create("load_to_docker",
            LoadToDockerExtension.class, project);
        project.getTasks().register("loadToDocker", LoadToDockerTask.class, extension);
    }
}
