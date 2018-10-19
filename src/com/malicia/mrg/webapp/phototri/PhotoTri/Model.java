package com.malicia.mrg.webapp.phototri.PhotoTri;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.stage.DirectoryChooser;
import javafx.stage.Window;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;


public class Model extends Window {

    public String getRepertory(){
        DirectoryChooser directoryChooser = new DirectoryChooser ();
        directoryChooser.setInitialDirectory(new File("d:/"));
        File selectedDirectory = directoryChooser.showDialog(this);
        return selectedDirectory.getAbsolutePath() ;
    }

    public void populateFile(ListView<?> fileSelect, String repertoire) {
        ObservableList data = FXCollections.observableArrayList();
        try (Stream<Path> paths = Files.walk(Paths.get(repertoire))) {
            paths
                    .filter(path -> Files.isRegularFile(path))
                    .filter(p -> filterSelectionFile(p))
                    .distinct()
                    .forEach(x -> data.add(x));
        } catch (IOException e) {
            e.printStackTrace();
        }
        fileSelect.setItems(data);
    }

    private boolean filterSelectionFile(Path p) {
        String fileLow = p.toString().toLowerCase();
        return fileLow.endsWith(".jpg") || fileLow.endsWith(".jpeg") || fileLow.endsWith(".png") || fileLow.endsWith(".mp4") || fileLow.endsWith(".arw");
    }

    public void populateRepertory(ListView<?> folderSelect, String repertoire, ProgressBar progressBar) {
        ObservableList data = FXCollections.observableArrayList();
        try (Stream<Path> paths = Files.walk(Paths.get(repertoire))) {
            paths
                    .filter( Files::isDirectory)
                    .forEach((x -> analyseFile(x,data)));
        } catch (IOException e) {
            e.printStackTrace();
        }
        folderSelect.setItems(data);
        progressBar.setDisable(true);

    }

    private void analyseFile(Path x, ObservableList data) {
        data.add(x);
    }

    private void alertinfo(String title,String header,String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }


}
