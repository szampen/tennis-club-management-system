package com.tennis.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class DatabaseConnection {
    private static String url;
    private static String username;
    private static String password;
    private static BlockingQueue<Connection> pool; // queue for database connections, that allows to wait if all connections are being used

    public static void initialize(Properties config) throws Exception{
        url = config.getProperty("db.url");
        username = config.getProperty("db.username");
        password = config.getProperty("db.password");
        int poolSize = Integer.parseInt(config.getProperty("db.poolsize", "10"));

        pool = new ArrayBlockingQueue<>(poolSize);

        for (int i = 0; i < poolSize; i++) {
            pool.add(createConnection());
        }
    }

    public static Connection createConnection() throws Exception{
        Class.forName("com.mysql.cj.jdbc.Driver"); // driver for MySQL
        return DriverManager.getConnection(url,username,password);
    }

    public static Connection getConnection() throws Exception{
        Connection conn = pool.take(); // if all connections are being used, then waits for one to be returned
        if(conn.isClosed()){
            conn = createConnection(); // creates new connection when one has been closed, ex. timeout
        }
        return conn;
    }

    public static void returnConnection(Connection conn){
        try{
            if(conn != null && !conn.isClosed()){
                pool.put(conn); // return connection to the queue instead of closing it, waits for space if queue is full
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void shutdown(){
        for(Connection conn : pool){
            try{
                conn.close();
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
