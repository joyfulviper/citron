package org.prgrms.config;

import com.zaxxer.hikari.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseConnectionPool {
    //
    private static final HikariConfig config = new HikariConfig();
    private static final HikariDataSource ds;
    private static final Logger log = LoggerFactory.getLogger(DatabaseConnectionPool.class);

    static {
        config.setJdbcUrl("jdbc:mysql://localhost:3306/citron");
        config.setUsername("admin");
        config.setPassword("1234");
        config.setMaximumPoolSize(10);  // 풀의 최대 크기
        config.setMinimumIdle(5);  // 최소한으로 유지할 커넥션 수
        ds = new HikariDataSource(config);
        log.info("DATABASE IS CONNECTED: {}", config.getJdbcUrl());
    }

    private DatabaseConnectionPool() {}

    public static Connection getConnection() throws SQLException {
        return ds.getConnection();
    }
}