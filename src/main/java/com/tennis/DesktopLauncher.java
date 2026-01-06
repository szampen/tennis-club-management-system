package com.tennis;

import com.tennis.database.DatabaseConnection;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class DesktopLauncher extends Application {
    private ConfigurableApplicationContext springContext;

    @Override
    public void init() throws Exception {
        try {
            DatabaseConnection.initialize(loadConfig());
        } catch (Exception e) {
            System.err.println("Cannot initialize database.");
            System.exit(1);
        }
        //starting spring while initializing fxapp
        springContext = new SpringApplicationBuilder(TennisApp.class).run();
    }

    @Override
    public void start(Stage stage) throws Exception {
        WebView webView = new WebView();
        // loading local spring server
        webView.getEngine().load("http://localhost:8080");

        stage.setScene(new Scene(webView,1024,768));
        stage.setTitle("Tennis Club Management System");
        stage.show();
    }

    @Override
    public void stop() throws Exception {
        springContext.close();
        System.exit(0);
    }

    private static Properties loadConfig() throws IOException {
        Properties props = new Properties();
        try (FileInputStream fis = new FileInputStream("src/main/resources/application.properties")) {
            props.load(fis);
        } catch (IOException e) {
            throw new IOException();
        }
        return props;
    }

    public static void main(String[] args){
        launch(args);
    }
}
