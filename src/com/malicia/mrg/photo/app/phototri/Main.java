package com.malicia.mrg.photo.app.phototri;

import com.malicia.mrg.sqlite.SQLiteJDBCDriverConnection;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {


    private static Stage pStage;

    @Override
    public void start(Stage primaryStage) throws Exception{
        SQLiteJDBCDriverConnection sql = new SQLiteJDBCDriverConnection();
        sql.connect();
    sql.select("select  min(e.captureTime) , max(e.captureTime) , c.absolutePath , b.pathFromRoot  from AgLibraryFile a " +
                    "inner join AgLibraryFolder b " +
                    "on a.folder = b.id_local " +
                    "inner join AgLibraryRootFolder c " +
                    "on b.rootFolder = c.id_local " +
                    "inner join Adobe_images e " +
                    "on a.id_local = e.rootFile " +
            "group by  c.absolutePath , b.pathFromRoot");
        while (sql.rs.next()) {
            System.out.println(sql.rs.getString(1) +  "\t" +
                    sql.rs.getString(2) + "\t" +
                    sql.rs.getString(3) + sql.rs.getString(4));
        }


        Parent root = FXMLLoader.load(getClass().getResource("Master.fxml"));
        pStage = primaryStage;
        primaryStage.setTitle("Photo Tri");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }

    public static Stage getPrimaryStage() {
        return pStage;
    }

}
