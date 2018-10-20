package com.malicia.mrg.photo.app.phototri;

import com.malicia.mrg.photo.object.groupphoto.GroupeDePhoto;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.stage.DirectoryChooser;
import javafx.stage.Window;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;


public class Model extends Window {

    private GroupeDePhoto grpPhotoNew;
    private ObservableList<GroupeDePhoto> grpListPhotoRepertoire;

    public String getRepertory(){
        DirectoryChooser directoryChooser = new DirectoryChooser ();
        directoryChooser.setInitialDirectory(new File("d:/"));
        File selectedDirectory = directoryChooser.showDialog(this);
        return selectedDirectory.getAbsolutePath() ;
    }

    public ObservableList<String> populateFile( String repertoire) {
        grpPhotoNew = new GroupeDePhoto();
        try (Stream<Path> paths = Files.walk(Paths.get(repertoire))) {
            paths
                    .filter(path -> Files.isRegularFile(path))
                    .distinct()
                    .forEach((x -> grpPhotoNew.addfile(x.toString())));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return grpPhotoNew.getListFiles();
    }


    public ObservableList<GroupeDePhoto> populateRepertory(String repertoire) {
        ObservableList<GroupeDePhoto> grpListPhotoRepertoire = FXCollections.observableArrayList();
        try (Stream<Path> paths = Files.walk(Paths.get(repertoire))) {
            paths
                    .filter( Files::isDirectory)
                    .distinct()
                    .forEach((x -> grpListPhotoRepertoire.add(new GroupeDePhoto(x.toString()))));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return grpListPhotoRepertoire;
    }


    private void alertinfo(String title,String header,String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }


}
