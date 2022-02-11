# Overview

Maven and gradle plugins to publish a docker tarball (e.g. created by jib) to the docker daemon via named pipe (Docker
Windows) or unix domain socket (Linux). With this plugin you can publish your images without the need to install the
docker CLI.

# Usage with Maven

Example configuration in maven (including jib):

```xml

<build>
    <plugins>
        <plugin>
            <groupId>com.google.cloud.tools</groupId>
            <artifactId>jib-maven-plugin</artifactId>
            <executions>
                <execution>
                    <id>create-tar</id>
                    <phase>package</phase>
                    <goals>
                        <goal>buildTar</goal>
                    </goals>
                    <configuration>
                        <from>
                            <image>
                                amazoncorretto:17
                            </image>
                        </from>
                        <to>
                            <image>
                                my-application
                            </image>
                            <tags>
                                <tag>latest</tag>
                            </tags>
                        </to>
                        <outputPaths>
                            <tar>target/jib-image.tar</tar>
                        </outputPaths>
                    </configuration>
                </execution>
            </executions>
        </plugin>
        <plugin>
            <groupId>com.prime157.docker</groupId>
            <artifactId>load-to-docker-maven-plugin</artifactId>
            <executions>
                <execution>
                    <phase>package</phase>
                    <goals>
                        <goal>load-to-docker</goal>
                    </goals>
                    <configuration>
                        <tarball>target/jib-image.tar</tarball>
                    </configuration>
                </execution>
            </executions>
        </plugin>
    </plugins>
</build>
```

* The jib plugin is configured to create a tarball of the image layers and stores it in `target/jib-image.tar`
* The load-to-docker-maven-plugin is configured to push the tarball from `target/jib-image.tar` to docker. On Windows
  the plugin will talk to docker via the named pipe `\\.\pipe\docker_engine`. On linux via the unix domain
  socket `/var/run/docker.sock`.

# Usage with Gradle

Example configuration with gradle (assuming you have configured jib to generate the image tarball
in `build/jib-image.tar`):

```groovy
plugins {
  id 'com.prime157.load-to-docker' version '0.1-SNAPSHOT'
}
load_to_docker {
  tarball = 'build/jib-image.tar'
}
```

* The load-to-docker plugin is configured to push the tarball from `build/jib-image.tar` to docker. On Windows the
  plugin will talk to docker via the named pipe `\\.\pipe\docker_engine`. On linux via the unix domain
  socket `/var/run/docker.sock`.
* The tarball can either be an absolute path or a path relative to the project's root path.
* The task is called `other/load-to-docker`
