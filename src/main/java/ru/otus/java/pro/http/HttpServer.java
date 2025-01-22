package ru.otus.java.pro.http;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.StandardCharsets;
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
    private static final Charset CHARSET = StandardCharsets.UTF_8;
    private static final CharsetDecoder DECODER = CHARSET.newDecoder();
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
                    buffer.clear();
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
            CharBuffer charBuffer = DECODER.decode(buffer);
            String method = readToken(charBuffer);
            String uri = readToken(charBuffer);
            skipLine(charBuffer);

            Map<String, String> headers = new HashMap<>();
            while (charBuffer.hasRemaining()) {
                String line = readLine(charBuffer);
                if (line.isEmpty()) {
                    break;
                }
                int colonIndex = line.indexOf(": ");
                if (colonIndex != -1) {
                    headers.put(line.substring(0, colonIndex), line.substring(colonIndex + 2));
                }
            }

            Map<String, String> parameters = new HashMap<>();
            if (uri.contains("?")) {
                int questionMarkIndex = uri.indexOf("?");
                String[] params = uri.substring(questionMarkIndex + 1).split("&");
                uri = uri.substring(0, questionMarkIndex);
                for (String param : params) {
                    int equalsIndex = param.indexOf("=");
                    if (equalsIndex != -1) {
                        parameters.put(param.substring(0, equalsIndex), param.substring(equalsIndex + 1));
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

    private String readToken(CharBuffer buffer) {
        StringBuilder token = new StringBuilder();
        while (buffer.hasRemaining()) {
            char c = buffer.get();
            if (c == ' ' || c == '\r' || c == '\n') {
                break;
            }
            token.append(c);
        }
        return token.toString();
    }

    private void skipLine(CharBuffer buffer) {
        while (buffer.hasRemaining()) {
            char c = buffer.get();
            if (c == '\n') {
                break;
            }
        }
    }

    private String readLine(CharBuffer buffer) {
        StringBuilder line = new StringBuilder();
        while (buffer.hasRemaining()) {
            char c = buffer.get();
            if (c == '\r') {
                continue;
            }
            if (c == '\n') {
                break;
            }
            line.append(c);
        }
        return line.toString();
    }

    private void sendResponse(SocketChannel socketChannel, String response) throws IOException {
        ByteBuffer buffer = ByteBuffer.wrap(response.getBytes(CHARSET));
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