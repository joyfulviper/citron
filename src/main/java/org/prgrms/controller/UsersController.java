package org.prgrms.controller;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.prgrms.repository.Users;
import org.prgrms.repository.UsersRepository;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

import static org.prgrms.utils.JsonParser.extractValueFromJson;


public class UsersController {

    private final UsersRepository usersRepository;

    public UsersController(UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
    }

    private String getRequestBody(HttpExchange exchange) throws IOException {
        InputStream inputStream = exchange.getRequestBody();
        StringBuilder sb = new StringBuilder();
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }
        return sb.toString();
    }

    public class CreateHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            // 요청 본문 읽기
            String requestBody = getRequestBody(exchange);

            // "name" 값을 수동으로 추출
            String name = extractValueFromJson(requestBody, "name");
            String[] parts = requestBody.split(":");
            if (parts.length == 2 && parts[0].trim().equals("\"name\"")) {
                name = parts[1].trim().replace("\"", "");
            }

            if (name == null) {
                String response = "badRequest";
                exchange.sendResponseHeaders(400, response.length());
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(response.getBytes());
                }
                return;
            }

            // 새로운 Users 객체 생성 및 저장
            Users newUser = new Users(UUID.randomUUID(), name);
            usersRepository.save(newUser);

            // 응답 설정 및 전송
            String response = "Created";
            exchange.sendResponseHeaders(201, response.length());
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        }
    }

    public class ReadHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("GET".equals(exchange.getRequestMethod())) {
                List<Users> allUsers = usersRepository.findAll();
                StringBuilder sb = new StringBuilder();

                sb.append("[");
                for (int i = 0; i < allUsers.size(); i++) {
                    Users user = allUsers.get(i);
                    sb.append("{");
                    sb.append("\"id\": \"").append(user.getId()).append("\", ");
                    sb.append("\"name\": \"").append(user.getName()).append("\"");
                    sb.append("}");

                    if (i < allUsers.size() - 1) {
                        sb.append(", ");
                    }
                }
                sb.append("]");

                String response = sb.toString();

                exchange.sendResponseHeaders(200, response.length());
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(response.getBytes());
                }
            }
        }
    }


    public class UpdateHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("PATCH".equals(exchange.getRequestMethod())) {
                // 요청 본문 읽기
                String requestBody = getRequestBody(exchange);

                // "name" 값을 수동으로 추출
                String name = extractValueFromJson(requestBody, "name");
                String[] parts = requestBody.split(":");
                if (parts.length == 2 && parts[0].trim().equals("\"name\"")) {
                    name = parts[1].trim().replace("\"", "");
                }

                if (name == null) {
                    String response = "badRequest";
                    exchange.sendResponseHeaders(400, response.length());
                    try (OutputStream os = exchange.getResponseBody()) {
                        os.write(response.getBytes());
                    }
                    return;
                }

                // URI로부터 UUID 추출
                String path = exchange.getRequestURI().getPath();
                String[] pathParts = path.split("/");
                UUID uuid = UUID.fromString(pathParts[pathParts.length - 1]);

                // UUID를 사용하여 Users 객체 업데이트
                usersRepository.update(uuid, name);

                // 응답 설정 및 전송
                String response = "Updated";
                exchange.sendResponseHeaders(200, response.length());
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(response.getBytes());
                }
            }
        }
    }


    public class DeleteHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("DELETE".equals(exchange.getRequestMethod())) {
                // Simulate deleting all users
                usersRepository.deleteAll();

                String response = "";
                exchange.sendResponseHeaders(204, -1);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(response.getBytes());
                }
            }
        }
    }
}