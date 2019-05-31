package com.ef.services;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created by prest on 5/30/2019.
 */
public class DBConnection {

    // JDBC driver name and database URL
    static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://localhost/log_entry?serverTimezone=UTC";

    //  Database credentials
    static final String USER = "root";
    static final String PASS = "root";
    static final String ENTRIES_TBL = "entries";
    static final String BLOCKED_IP_TBL = "blocked_ip";

    static Connection connection;

    public static Connection getConnection(){

        if(DBConnection.connection != null){

            return DBConnection.connection;
        }

        try {
            Class.forName(DBConnection.JDBC_DRIVER);
            connection = DriverManager.getConnection(DBConnection.DB_URL, USER, PASS);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            return connection;
        }

    }


}
