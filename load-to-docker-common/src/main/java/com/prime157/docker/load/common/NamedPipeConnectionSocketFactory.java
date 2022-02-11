package com.prime157.docker.load.common;

import java.net.Socket;
import java.nio.file.Path;

import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.protocol.HttpContext;

public class NamedPipeConnectionSocketFactory extends PlainConnectionSocketFactory {
    private final Path path;

    public NamedPipeConnectionSocketFactory(final Path path) {
        this.path = path;
    }

    @Override
    public Socket createSocket(final HttpContext context) {
        return new NamedPipeSocket(path);
    }
}
