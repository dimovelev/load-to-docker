package com.prime157.docker.load.common;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousByteChannel;
import java.nio.channels.AsynchronousCloseException;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.CompletionHandler;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

import static java.nio.channels.Channels.newInputStream;
import static java.nio.channels.Channels.newOutputStream;
import static java.nio.file.StandardOpenOption.READ;
import static java.nio.file.StandardOpenOption.WRITE;

public class NamedPipeSocket extends Socket {
    private final Path path;
    private NamedPipeChannel channel;

    public NamedPipeSocket(final Path path) {
        this.path = path;
    }

    @Override
    public void close() throws IOException {
        if (channel != null) {
            channel.close();
        }
    }

    @Override
    public void connect(final SocketAddress endpoint, final int timeout) throws IOException {
        if (timeout != 0) {
            throw new IllegalArgumentException("Timeout cannot be set. Please use 0.");
        }
        final AsynchronousFileChannel fileChannel = AsynchronousFileChannel.open(path, READ, WRITE);
        channel = new NamedPipeChannel(fileChannel);
    }

    @Override
    public InputStream getInputStream() {
        return newInputStream(channel);
    }

    @Override
    public OutputStream getOutputStream() {
        return newOutputStream(channel);
    }

    private static class NamedPipeChannel implements AsynchronousByteChannel {
        private final AsynchronousFileChannel channel;

        NamedPipeChannel(final AsynchronousFileChannel channel) {
            this.channel = channel;
        }

        @Override
        public <A> void read(final ByteBuffer dst, final A attachment,
            final CompletionHandler<Integer, ? super A> handler) {
            channel.read(dst, 0, attachment, new AsyncCloseCompletionHandler<>(handler));
        }

        @Override
        public Future<Integer> read(final ByteBuffer dst) {
            final CompletableFutureHandler result = new CompletableFutureHandler();
            channel.read(dst, 0, null, result);
            return result;
        }

        @Override
        public <A> void write(final ByteBuffer src, final A attachment,
            final CompletionHandler<Integer, ? super A> handler) {
            channel.write(src, 0, attachment, handler);
        }

        @Override
        public Future<Integer> write(final ByteBuffer src) {
            return channel.write(src, 0);
        }

        @Override
        public void close() throws IOException {
            channel.close();
        }

        @Override
        public boolean isOpen() {
            return channel.isOpen();
        }

        private static class CompletableFutureHandler extends CompletableFuture<Integer> implements
            CompletionHandler<Integer, Object> {
            @Override
            public void completed(Integer read, Object attachment) {
                complete(read > 0 ? read : -1);
            }

            @Override
            public void failed(Throwable exc, Object attachment) {
                if (exc instanceof AsynchronousCloseException) {
                    complete(-1);
                    return;
                }
                completeExceptionally(exc);
            }
        }
        private static class AsyncCloseCompletionHandler<A> implements CompletionHandler<Integer, A> {
            private final CompletionHandler<Integer, ? super A> handler;

            public AsyncCloseCompletionHandler(final CompletionHandler<Integer, ? super A> handler) {
                this.handler = handler;
            }

            @Override
            public void completed(Integer read, A attachment) {
                handler.completed(read > 0 ? read : -1, attachment);
            }

            @Override
            public void failed(Throwable exc, A attachment) {
                if (exc instanceof AsynchronousCloseException) {
                    // EOF
                    handler.completed(-1, attachment);
                } else {
                    handler.failed(exc, attachment);
                }
            }
        }
    }
}
