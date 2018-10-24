package com.malicia.mrg.photo.app.phototri;

import com.malicia.mrg.object.MessagePerso;
import com.malicia.mrg.object.Toast;
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
        try (Stream<Path> paths = Files.walk(Paths.get(repertoire),1)) {
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

//AgLibraryRootFolder
//AgLibraryFolder
//AgLibraryFile
//Adobe_images
//Adobe_imageProperties
//
//select  min(e.captureTime) , max(e.captureTime) , c.absolutePath , b.pathFromRoot  from AgLibraryFile a
//inner join AgLibraryFolder b
//on a.folder = b.id_local
//inner join AgLibraryRootFolder c
//on b.rootFolder = c.id_local
//inner join Adobe_images e
//on a.id_local = e.rootFile
//group by  c.absolutePath , b.pathFromRoot

 //       MessagePerso.makeText(Main.getPrimaryStage(),"populateRepertory") ;
        ObservableList<GroupeDePhoto> grpListPhotoRepertoire = FXCollections.observableArrayList();
        try (Stream<Path> paths = Files.walk(Paths.get(repertoire))) {
            paths
                    .filter( Files::isDirectory)

                    .distinct()
                    .forEach((x -> isAdd(grpListPhotoRepertoire, x)));
        } catch (IOException e) {
            e.printStackTrace();
        }
        java.util.Collections.sort(grpListPhotoRepertoire,(o1, o2) -> GroupeDePhoto.compare(o1,o2) );
 //       MessagePerso.killText();
        return grpListPhotoRepertoire;
    }

    private void isAdd(ObservableList<GroupeDePhoto> grpListPhotoRepertoire, Path x) {

        String pathString = x.toString().toLowerCase();
        if (!pathString.contains("rejet") &&
            !pathString.contains("@") &&
            !pathString.contains("&") &&
            !pathString.contains("!!") ) {
            GroupeDePhoto dePhoto = new GroupeDePhoto(x.toString());
            //       MessagePerso.addText(dePhoto.toStringInfo());
            grpListPhotoRepertoire.add(dePhoto);
        };
    }


    private void alertinfo(String title,String header,String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }


}
