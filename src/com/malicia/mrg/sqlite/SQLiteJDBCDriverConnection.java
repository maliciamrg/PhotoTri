package com.malicia.mrg.sqlite;

import java.sql.*;

/**
 *
 * @author sqlitetutorial.net
 */
public class SQLiteJDBCDriverConnection {
    public static Connection conn;
    public ResultSet rs;

    /**
     * Connect to a sample database
     */
    public static void connect(String sqlliteDatabase) {
        conn = null;
        try {
            // db parameters
            String url = "jdbc:sqlite:"+sqlliteDatabase;
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
    public int executeUpdate(String sql ){


        Statement stmt  = null;
        try {
            stmt = conn.createStatement();

            int ret = stmt.executeUpdate(sql);

            return ret;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * select all rows in the warehouses table
     */
    public boolean execute(String sql ){


        Statement stmt  = null;
        try {
            stmt = conn.createStatement();

            boolean ret = stmt.execute(sql);
            int nb = stmt.getUpdateCount();
//            conn.commit();
            return ret;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
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