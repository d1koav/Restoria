package com.example.restoria;

import android.content.Context;
import android.database.Cursor;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseHelper {
    private static final String DB_URL = "jdbc:postgresql://192.168.0.121:5432/restoria";

    private static final String USER = "postgres";
    private static final String PASSWORD = "mysecretpassword";

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("org.postgresql.Driver");
            return DriverManager.getConnection(DB_URL, USER, PASSWORD);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new SQLException("Failed to connect to SQL Server");
        }
    }
    public static void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}



