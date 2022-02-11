package com.prime157.docker.load.common;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.file.Path;

import org.apache.http.HttpHost;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.protocol.HttpContext;
import org.newsclub.net.unix.AFUNIXSocket;
import org.newsclub.net.unix.AFUNIXSocketAddress;

public class UnixDomainConnectionSocketFactory extends PlainConnectionSocketFactory {
    private final Path path;

    public UnixDomainConnectionSocketFactory(final Path path) {
        this.path = path;
    }

    @Override
    public Socket createSocket(HttpContext context) throws IOException {
        return AFUNIXSocket.newInstance();
    }

    @Override
    public Socket connectSocket(final int connectTimeout, final Socket socket, final HttpHost host,
        final InetSocketAddress remoteAddress, final InetSocketAddress localAddress, final HttpContext context) throws
        IOException {
        socket.connect(AFUNIXSocketAddress.of(path));
        return socket;
    }
}
