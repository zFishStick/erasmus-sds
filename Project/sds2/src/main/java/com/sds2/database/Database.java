package com.sds2.database;

import java.sql.*;
import java.util.logging.Logger;

import org.springframework.stereotype.Component;

@Component
public class Database {

    private Connection connection;
    private static final Logger logger = Logger.getLogger(Database.class.getName());

    public Database() {
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:database.db");
            logger.info("Database connected successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Connection getConnection() {
        return connection;
    }

    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
