package com.malicia.mrg.photo.app.phototri;

import com.malicia.mrg.sqlite.SQLiteJDBCDriverConnection;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class Main extends Application {


    public static Stage pStage;
    public static Parent root;
    public static Properties properties;

    @Override
    public void start(Stage primaryStage) throws Exception{
        root = FXMLLoader.load(getClass().getResource("Master.fxml"));
        pStage = primaryStage;
        primaryStage.setTitle("Photo Tri");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }


    public static void main(String[] args) {
        try (FileReader reader = new FileReader("resource/config.properties")){
            properties = new Properties();
            properties.load(reader);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        launch(args);
    }

    public static Stage getPrimaryStage() {
        return pStage;
    }

}
