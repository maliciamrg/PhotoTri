package com.malicia.mrg.webapp.phototri.PhotoTri;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
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
    private ListView<?> fileSelect;

    @FXML
    private ListView<?> folderSelect;

    @FXML
    private Text fileDateTime;

    @FXML
    private Button transfertFile;

    @FXML
    private ImageView imagefileSelect;

    @FXML
    private ProgressBar progressBar;

    @FXML
    void TransfertFile() {

    }

    @FXML
    void chooseRepertoryGroup() {
        ChooseRepertoryGroup.setText(mod.getRepertory());
        mod.populateRepertory(folderSelect ,ChooseRepertoryGroup.getText() ,progressBar);
    }

    @FXML
    void chooseRepertoryNew() {
        ChooseRepertoryNew.setText(mod.getRepertory());
        mod.populateFile(fileSelect ,ChooseRepertoryNew.getText() );
    }

    @FXML
    void previewPhoto() {
        FileInputStream input = null;
        String fichier = fileSelect.getSelectionModel().getSelectedItem().toString();
        try {
            input = new FileInputStream(fichier);
            Image image = new Image(input);
            imagefileSelect.setImage(image);
 //           fileDateTime.setText(mod.getExifFrom(fichier));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

}
