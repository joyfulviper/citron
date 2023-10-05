package org.prgrms;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.prgrms.controller.UsersController;
import org.prgrms.repository.UsersDBRepository;
import org.prgrms.repository.UsersRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

public class Main {

    private final static Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws IOException {
        UsersRepository usersRepository = new UsersDBRepository();//new UsersRepositoryImpl(new ConcurrentHashMap<>());
        UsersController usersController = new UsersController(usersRepository);
        UsersHandler usersHandler = new UsersHandler(usersController);
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

        server.createContext("/", usersHandler);
        server.setExecutor(Executors.newFixedThreadPool(200));
        server.start();
        log.info("server is started on {}", server.getAddress());
        log.info("Thread info: {}", server.getExecutor());
    }

    static class UsersHandler implements HttpHandler {
        private final UsersController usersController;

        UsersHandler(UsersController usersController) {
            this.usersController = usersController;
        }

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String method = exchange.getRequestMethod();

            if ("POST".equalsIgnoreCase(method)) {
                var handler = usersController.new CreateHandler();
                handler.handle(exchange);
            } else if ("GET".equalsIgnoreCase(method)) {
                var handler = usersController.new ReadHandler();
                handler.handle(exchange);
            } else if ("PATCH".equalsIgnoreCase(method)) {
                var handler = usersController.new UpdateHandler();
                handler.handle(exchange);
            } else if ("DELETE".equalsIgnoreCase(method)) {
                var handler = usersController.new DeleteHandler();
                handler.handle(exchange);
            } else {
                String response = "Unsupported method";
                exchange.sendResponseHeaders(405, response.length());
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(response.getBytes());
                }
            }
        }
    }
}