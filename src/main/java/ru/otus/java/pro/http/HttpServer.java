package ru.otus.java.pro.http;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
public class HttpServer {
    private static final int BUFFER_SIZE = 8192;
    private static final int MAX_REQUEST_SIZE = 5 * 1024 * 1024;
    private final int port;
    private final ExecutorService threadPool;
    private volatile boolean running = true;

    public HttpServer(int port, int threadPoolSize) {
        this.port = port;
        this.threadPool = Executors.newFixedThreadPool(threadPoolSize);
    }

    public void start() throws IOException {
        Selector selector = Selector.open();
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.bind(new InetSocketAddress(port));
        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        while (running) {
            selector.select();
            Iterator<SelectionKey> keyIterator = selector.selectedKeys().iterator();
            while (keyIterator.hasNext()) {
                SelectionKey key = keyIterator.next();
                keyIterator.remove();

                if (key.isAcceptable()) {
                    handleAccept(key);
                } else if (key.isReadable()) {
                    handleRead(key);
                }
            }
        }

        serverSocketChannel.close();
        selector.close();
        threadPool.shutdown();
    }

    private void handleAccept(SelectionKey key) throws IOException {
        ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
        SocketChannel socketChannel = serverSocketChannel.accept();
        socketChannel.configureBlocking(false);
        socketChannel.register(key.selector(), SelectionKey.OP_READ, ByteBuffer.allocate(BUFFER_SIZE));
    }

    private void handleRead(SelectionKey key) {
        SocketChannel socketChannel = (SocketChannel) key.channel();
        ByteBuffer buffer = (ByteBuffer) key.attachment();
        threadPool.submit(() -> {
            try {
                if (!socketChannel.isOpen() || !socketChannel.isConnected()) {
                    return;
                }

                int bytesRead = socketChannel.read(buffer);
                if (bytesRead == -1) {
                    socketChannel.close();
                    return;
                }

                if (buffer.position() > MAX_REQUEST_SIZE) {
                    sendResponse(socketChannel, "HTTP/1.1 413 Payload Too Large\r\n\r\n");
                    socketChannel.close();
                    return;
                }

                if (buffer.position() > 0 && buffer.get(buffer.position() - 1) == '\n') {
                    buffer.flip();
                    HttpRequest request = parseRequest(buffer);
                    if (request != null) {
                        if ("/shutdown".equals(request.getUri()) && "GET".equals(request.getMethod())) {
                            sendResponse(socketChannel, "HTTP/1.1 200 OK\r\n\r\nServer is shutting down.");
                            running = false;
                        } else {
                            sendResponse(socketChannel, "HTTP/1.1 200 OK\r\n\r\n");
                        }
                    } else {
                        sendResponse(socketChannel, "HTTP/1.1 500 Internal Server Error\r\n\r\n");
                    }
                    socketChannel.close();
                }
            } catch (IOException e) {
                log.error("Error handling read", e);
                try {
                    if (socketChannel.isOpen()) {
                        socketChannel.close();
                    }
                } catch (IOException ex) {
                    log.error("Error closing socket channel", ex);
                }
            }
        });
    }

    private HttpRequest parseRequest(ByteBuffer buffer) {
        try {
            String requestString = new String(buffer.array(), 0, buffer.limit());
            String[] lines = requestString.split("\r\n");
            if (lines.length < 1) {
                throw new IllegalArgumentException("Invalid HTTP request");
            }

            String[] requestLine = lines[0].split(" ");
            if (requestLine.length < 2) {
                throw new IllegalArgumentException("Invalid HTTP request line");
            }

            String method = requestLine[0];
            String uri = requestLine[1];

            Map<String, String> headers = new HashMap<>();
            int i = 1;
            while (i < lines.length && !lines[i].isEmpty()) {
                String[] header = lines[i].split(": ");
                if (header.length == 2) {
                    headers.put(header[0], header[1]);
                }
                i++;
            }

            Map<String, String> parameters = new HashMap<>();
            if (uri.contains("?")) {
                String[] uriParts = uri.split("\\?");
                uri = uriParts[0];
                String[] params = uriParts[1].split("&");
                for (String param : params) {
                    String[] keyValue = param.split("=");
                    if (keyValue.length == 2) {
                        parameters.put(keyValue[0], keyValue[1]);
                    }
                }
            }

            HttpRequest request = new HttpRequest();
            request.setMethod(method);
            request.setUri(uri);
            request.setHeaders(headers);
            request.setParameters(parameters);

            return request;
        } catch (Exception e) {
            log.error("Error parsing request", e);
            return null;
        }
    }

    private void sendResponse(SocketChannel socketChannel, String response) throws IOException {
        ByteBuffer buffer = ByteBuffer.wrap(response.getBytes());
        while (buffer.hasRemaining()) {
            socketChannel.write(buffer);
        }
    }

    public static void main(String[] args) {
        try {
            Properties properties = new Properties();
            properties.load(HttpServer.class.getClassLoader().getResourceAsStream("server.properties"));

            int port = Integer.parseInt(properties.getProperty("server.port"));
            int threadPoolSize = Integer.parseInt(properties.getProperty("server.threadPoolSize"));

            HttpServer server = new HttpServer(port, threadPoolSize);
            server.start();
        } catch (IOException e) {
            log.error("Error starting server", e);
        }
    }
}