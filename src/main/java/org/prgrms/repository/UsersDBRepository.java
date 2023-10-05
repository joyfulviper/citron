package org.prgrms.repository;

import org.prgrms.config.DatabaseConnectionPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UsersDBRepository implements UsersRepository {

    private static final Logger log = LoggerFactory.getLogger(UsersDBRepository.class);
    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Failed to load MySQL driver", e);
        }
    }

    public UsersDBRepository() {
        initDatabase();
    }

    private void initDatabase() {
        try (Connection connection = DatabaseConnectionPool.getConnection()) {
            DatabaseMetaData meta = connection.getMetaData();
            try (ResultSet resultSet = meta.getTables(null, null, "users", new String[]{"TABLE"})) {
                if (!resultSet.next()) {
                    // 테이블이 없으면 생성
                    createTable(connection);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Database initialization error", e);
        }
    }

    private void createTable(Connection connection) {
        String sql = "CREATE TABLE users ("
                + "id VARCHAR(36) PRIMARY KEY, "
                + "name VARCHAR(255) NOT NULL)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.executeUpdate();
            log.info("Executed SQL: {}", sql);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create table", e);
        }
    }

    @Override
    public Users save(Users users) {
        try (Connection connection = DatabaseConnectionPool.getConnection()) {
            String sql = "INSERT INTO users (id, name) VALUES (?, ?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, users.getId().toString());
                preparedStatement.setString(2, users.getName());
                preparedStatement.executeUpdate();
                log.info("Executed SQL: {}", sql);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Database error", e);
        }
        return users;
    }

    @Override
    public List<Users> findAll() {
        List<Users> usersList = new ArrayList<>();
        try (Connection connection = DatabaseConnectionPool.getConnection()) {
            String sql = "SELECT * FROM users";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql);
                 ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    UUID id = UUID.fromString(resultSet.getString("id"));
                    String name = resultSet.getString("name");
                    usersList.add(new Users(id, name));
                }
            }
            log.info("Executed SQL: {}", sql);
        } catch (SQLException e) {
            throw new RuntimeException("Database error", e);
        }
        return usersList;
    }

    @Override
    public void update(UUID id, String name) {
        try (Connection connection = DatabaseConnectionPool.getConnection()) {
            String sql = "UPDATE users SET name = ? WHERE id = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, name);
                preparedStatement.setString(2, id.toString());
                preparedStatement.executeUpdate();
            }
            log.info("Executed SQL: {}", sql);
        } catch (SQLException e) {
            throw new RuntimeException("Database error", e);
        }
    }

    @Override
    public void deleteAll() {
        try (Connection connection = DatabaseConnectionPool.getConnection()) {
            String sql = "DELETE FROM users";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.executeUpdate();
            }
            log.info("Executed SQL: {}", sql);
        } catch (SQLException e) {
            throw new RuntimeException("Database error", e);
        }
    }
}