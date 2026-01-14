package com.tennis;

import com.tennis.database.DatabaseConnection;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;

import java.util.Properties;


@SpringBootApplication
public class TennisApp {
    public static void main(String[] args) {
        // Spring start - loading application.properties
        ConfigurableApplicationContext context = SpringApplication.run(TennisApp.class, args);

        // Fetching settings loaded by Spring
        Environment env = context.getBean(Environment.class);

        try {
            Properties props = new Properties();
            props.setProperty("db.url", env.getProperty("db.url"));
            props.setProperty("db.username", env.getProperty("db.username"));
            props.setProperty("db.password", env.getProperty("db.password"));
            props.setProperty("db.pool.size", env.getProperty("db.pool.size", "10"));

            // Initializing database
            DatabaseConnection.initialize(props);

            //shutdown hook for database
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                System.out.println("Electron/App is closing. Cleaning up database pool...");
                DatabaseConnection.shutdown();
            }));

        } catch (Exception e) {
            System.err.println("Cannot initialize database: " + e.getMessage());
            System.exit(1);
        }
    }

}
