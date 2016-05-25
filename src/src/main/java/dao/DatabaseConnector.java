package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created by Johan Lindström (jolindse@hotmail.com) on 2016-05-25.
 */
public class DatabaseConnector {

    private Connection conn = null;

    public DatabaseConnector() {
        // db parameters
        String url = "jdbc:sqlite:game.db";
        // create a connection to the database
        try {
            conn = DriverManager.getConnection(url);
            System.out.println("Database connection established.");
        } catch (SQLException e) {
            System.out.println("Error connecting to database: " + e.getStackTrace());
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }


    public void closeConnection() {
        try {
            conn.close();
            System.out.println("Database connection closed.");
        } catch (SQLException e) {
            System.out.println("Error closing database connection: " + e.getStackTrace());
        }

    }

}
