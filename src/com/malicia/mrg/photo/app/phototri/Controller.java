package com.malicia.mrg.photo.app.phototri;

import com.malicia.mrg.photo.exifreader.ExifReader;
import com.malicia.mrg.photo.object.groupphoto.GroupeDePhoto;

import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

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
    private ToggleButton choixDuSens;
    private FileInputStream fluxPreview;
    private Image imagePreview;

    @FXML
    void switchSens() {}


    @FXML
    private ObservableList<GroupeDePhoto> popRepertory;
    private ObservableList<String> popRepNew;

    public void initialize() {
        ChooseRepertoryNew.setText("D:\\50_Phototheque\\@New");
        chooseRepertoryNewFromChoose();
        ChooseRepertoryGroup.setText("D:\\70_Catalogs\\Catalog_Phototheque\\70_Catalog_Phototheque.lrcat");
        chooseRepertoryGroupFromChoose();
    }

    @FXML
    void chooseRepertoryGroup() {
        ChooseRepertoryGroup.setText(mod.getRepertory());
        chooseRepertoryGroupFromChoose();
    }

    private void chooseRepertoryGroupFromChoose() {
        folderSelect.getSelectionModel().clearSelection();
        folderSelect.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        //popRepertory = mod.populateRepertory(ChooseRepertoryGroup.getText());
        popRepertory = mod.populateRepertoryBySqllite(ChooseRepertoryGroup.getText());
        folderSelect.setItems(popRepertory);
    }

    @FXML
    void chooseRepertoryNew() {
        ChooseRepertoryNew.setText(mod.getRepertory());
        chooseRepertoryNewFromChoose();
    }
    @FXML
    void chooseRepertoryNewFromChoose() {
        popRepNew = mod.populateFile(ChooseRepertoryNew.getText());
        fileSelect.setItems(popRepNew);
        fileSelect.getItems().addListener(new ListChangeListener() {
            @Override
            public void onChanged(ListChangeListener.Change change) {
                isTransfertPossible();
                actionfileSelect();
            }
        });
    }

    @FXML
    void actionfileSelect() {

        folderSelect.getSelectionModel().clearSelection();
        transfertFile.setDisable(true);

        String fichier = fileSelect.getSelectionModel().getSelectedItem().toString();
        //ExifReader exi = new ExifReader(new String[]{fichier});

        try {
            fluxPreview = new FileInputStream(fichier);
            imagePreview = new Image(fluxPreview);
            imagefileSelect.setImage(imagePreview);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        String printImageTags = ExifReader.printImageTags(fichier);
        fileDateTime.setText(printImageTags);

        int i;
        for(i=0; i < popRepertory.size() ; i+=1)
        {
            GroupeDePhoto groupeDePhoto = popRepertory.get(i);
            if (groupeDePhoto.isElegible(printImageTags)) {
                folderSelect.getSelectionModel().select(i);
            }
        }

        isTransfertPossible();


    }

    @FXML
    void actionFolderSelect() {
        isTransfertPossible();
    }

    @FXML
    void actionTransfertFile() {


        try {
            ClassLoader classLoader = getClass().getClassLoader();
            File Rfile = new File(classLoader.getResource("404error.jpeg").getFile());
            fluxPreview = new FileInputStream(Rfile);
            imagePreview = new Image(fluxPreview);
            imagefileSelect.setImage(imagePreview);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        int numeroFileSelect = fileSelect.getSelectionModel().getSelectedIndex();
        int numeroRepertorySelect = folderSelect.getSelectionModel().getSelectedIndex();

        String source = popRepNew.get(numeroFileSelect);
        Path p = Paths.get(source);
        String file = p.getFileName().toString();
        GroupeDePhoto groupeDePhotoDest = popRepertory.get(numeroRepertorySelect);
        String dest = groupeDePhotoDest.getPath() + File.separator + file ;
        p = null;

        try {
            Path temp = Files.move
                    (Paths.get(source),
                            Paths.get(dest));

            groupeDePhotoDest.addfile(dest);
            folderSelect.refresh();

            //select next
            popRepNew.remove(numeroFileSelect);
            fileSelect.refresh();

            if (numeroFileSelect<popRepNew.size()) {
                fileSelect.getSelectionModel().select(numeroFileSelect);
            } else {
                fileSelect.getSelectionModel().select(popRepNew.size());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void isTransfertPossible() {
        transfertFile.setDisable(true);
        if ((fileSelect.getSelectionModel().getSelectedItems().size()==1)
                && (folderSelect.getSelectionModel().getSelectedItems().size()==1)){
            transfertFile.setDisable(false);
        }
    }
}
