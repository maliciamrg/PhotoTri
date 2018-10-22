package com.malicia.mrg.photo.app.phototri;

import com.malicia.mrg.photo.exifreader.ExifReader;
import com.malicia.mrg.photo.object.groupphoto.GroupeDePhoto;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.scene.control.ProgressBar;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class Controller {

    private Model mod = new Model();

    @FXML
    private Text ChooseRepertoryGroup;

    @FXML
    private Text ChooseRepertoryNew;

    @FXML
    private ListView<String> fileSelect;

    @FXML
    private ListView<GroupeDePhoto> folderSelect;

    @FXML
    private Text fileDateTime;

    @FXML
    private Button transfertFile;

    @FXML
    private ImageView imagefileSelect;

    @FXML
    void TransfertFile() {

    }

    @FXML
    void chooseRepertoryGroup() {
        folderSelect.getSelectionModel().clearSelection();
        folderSelect.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        ChooseRepertoryGroup.setText(mod.getRepertory());
        folderSelect.setItems(mod.populateRepertory(ChooseRepertoryGroup.getText()));
    }

    @FXML
    void chooseRepertoryNew() {
        ChooseRepertoryNew.setText(mod.getRepertory());
        fileSelect.setItems(mod.populateFile(ChooseRepertoryNew.getText()));
    }

    @FXML
    void previewPhoto() {

        folderSelect.getSelectionModel().clearSelection();

        FileInputStream input = null;
        String fichier = fileSelect.getSelectionModel().getSelectedItem().toString();
        try {
            //ExifReader exi = new ExifReader(new String[]{fichier});

            input = new FileInputStream(fichier);
            Image image = new Image(input);
            imagefileSelect.setImage(image);

            fileDateTime.setText(ExifReader.printImageTags (fichier));

            folderSelect.getSelectionModel().select();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

}
