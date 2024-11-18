package com.example.login;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Connect {

    private static final String URL = "jdbc:mysql://localhost:3306/limkokwing";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    public static Connection connectDb() {
        Connection connect = null;
        try {

            Class.forName("com.mysql.cj.jdbc.Driver");


            connect = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Database connection successful.");
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver not found.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("Failed to connect to the database.");
            e.printStackTrace();
        }
        return connect;
    }
}
