package com.malicia.mrg.sqlite;

import java.sql.*;

/**
 *
 * @author sqlitetutorial.net
 */
public class SQLiteJDBCDriverConnection {
    private static Connection conn;
    public ResultSet rs;

    /**
     * Connect to a sample database
     */
    public static void connect() {
        conn = null;
        try {
            // db parameters
            String url = "jdbc:sqlite:C:\\Users\\professorX\\Desktop\\70_Catalog_Phototheque.lrcat";
            // create a connection to the database
            conn = DriverManager.getConnection(url);

            System.out.println("Connection to SQLite has been established.");

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    public static void disconnect() {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
        }
    }

    /**
     * select all rows in the warehouses table
     */
    public void select(String sql ){


        Statement stmt  = null;
        try {
            stmt = conn.createStatement();

            rs= stmt.executeQuery(sql);


        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}