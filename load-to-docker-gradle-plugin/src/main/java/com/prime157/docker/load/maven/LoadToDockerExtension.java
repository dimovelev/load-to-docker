package com.prime157.docker.load.maven;

import org.gradle.api.Project;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;

public class LoadToDockerExtension {
    private Property<String> tarball;

    public LoadToDockerExtension(final Project project) {
        final ObjectFactory factory = project.getObjects();
        tarball = factory.property(String.class);
//            .convention(JavaPlugin.RUNTIME_CLASSPATH_CONFIGURATION_NAME);
    }

//    @Input
    public String getTarball() {
        return tarball.getOrNull();
    }

    public void setTarball(final String tarball) {
        this.tarball.set(tarball);
    }
}
