package com.malicia.mrg.photo.app.phototri;

import com.malicia.mrg.photo.exifreader.ExifReader;
import com.malicia.mrg.photo.object.groupphoto.GroupeDePhoto;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;

import java.awt.*;
import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Controller {

    private Model mod = new Model();

    @FXML
    private Text TextDatabaseLigthroom;

    @FXML
    private ListView<GroupeDePhoto> ListFichierNew;

    @FXML
    private ListView<GroupeDePhoto> ListRepertoirePhoto;

    @FXML
    private Button BouttonTransfertFile;
    @FXML
    private ToggleButton ToggleMasterSelection;

    private boolean MasterSelectionNew;
    private ObservableList<GroupeDePhoto> popFichierNew;
    private ObservableList<GroupeDePhoto> popFichierNewOri;
    private ObservableList<GroupeDePhoto> popRepertoirePhotoOri;


    @FXML
    void openFolder() {
        Desktop desktop = Desktop.getDesktop();
        try {
            desktop.open(new File(ListRepertoirePhoto.getSelectionModel().getSelectedItem().getPath()) );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void playElement() {
        Desktop desktop = Desktop.getDesktop();
        try {
            desktop.open(new File(ListFichierNew.getSelectionModel().getSelectedItem().getPath()) );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void changeMasterSelection() {
        ListFichierNew.getSelectionModel().clearSelection();
        ListRepertoirePhoto.getSelectionModel().clearSelection();
        if (MasterSelectionNew){
            ToggleMasterSelection.setText("Master selection #Folder");
            MasterSelectionNew = false;
            ListRepertoirePhoto.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
            ListFichierNew.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        } else {
            ToggleMasterSelection.setText("Master selection #New");
            MasterSelectionNew = true;
            ListRepertoirePhoto.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
            ListFichierNew.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        }
        reconstructionListFichierNew();
        reconstructionListRepertoirePhoto();
    }


    @FXML
    private ObservableList<GroupeDePhoto> popRepertoirePhoto;

    public void initialize() {
        TextDatabaseLigthroom.setText("D:\\70_Catalogs\\Catalog_Phototheque\\70_Catalog_Phototheque.lrcat");
        MasterSelectionNew = false;
        populateListFichierNew();
        populateListRepertoirePhoto();
        changeMasterSelection();
    }

    @FXML
    void selectLigthroomDatabase() {
        TextDatabaseLigthroom.setText(mod.chooseFileLrcat());
        populateListRepertoirePhoto();
    }

    private void populateListRepertoirePhoto() {
        popRepertoirePhotoOri = mod.populateRepertoirePhotoBySqllite(TextDatabaseLigthroom.getText());

        reconstructionListRepertoirePhoto();
    }


    @FXML
    private void populateListFichierNew() {

        popFichierNewOri = mod.populateFichierPhotoBySqllite(TextDatabaseLigthroom.getText());

        reconstructionListFichierNew();

        ListFichierNew.setCellFactory(param -> new ListCell<GroupeDePhoto>() {
            private ImageView imageView = new ImageView();
            @Override
            public void updateItem(GroupeDePhoto name, boolean empty) {
                super.updateItem(name, empty);
                if (empty) {
                    setText(null);
                    setGraphic(null);
                } else {
                    try {
                        imageView.setImage(new Image(new FileInputStream(name.getPath())));
                        imageView.setFitWidth(120);
                        imageView.setPreserveRatio(true);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    setText(name.toString());
                    setGraphic(imageView);
                }
            }
        });
    }

    //TODO
    @FXML
    void actionFichierNew() {
        if (MasterSelectionNew) {

            ListRepertoirePhoto.getSelectionModel().clearSelection();
            BouttonTransfertFile.setDisable(true);

            String fichierDateyyyymmjj = ListFichierNew.getSelectionModel().getSelectedItem().getDateDebyyyymmjj();

            int i;
            for (i = 0; i < popRepertoirePhoto.size(); i += 1) {
                GroupeDePhoto groupeDePhoto = popRepertoirePhoto.get(i);
                if (groupeDePhoto.isElegible(fichierDateyyyymmjj)) {
                    ListRepertoirePhoto.getSelectionModel().select(i);
                }
            }

        }

        isTransfertPossible();

    }


    @FXML
    void actionRepertoirePhoto() {
        if (!MasterSelectionNew) {

            reconstructionListFichierNew();
            BouttonTransfertFile.setDisable(true);

            GroupeDePhoto groupeDePhoto = ListRepertoirePhoto.getSelectionModel().getSelectedItem();

            int i;
            for (i = 0; i < popFichierNew.size(); i += 1) {
                String fichierDateyyyymmjj = popFichierNew.get(i).getDateDebyyyymmjj();
                if (groupeDePhoto.isElegible(fichierDateyyyymmjj)) {
                    ListFichierNew.getSelectionModel().select(i);
                } else {
                    popFichierNew.remove(i);
                    i-=1;
                }
            }
            ListFichierNew.setItems(popFichierNew);
            ListFichierNew.refresh();

        }

        isTransfertPossible();

    }

    private void reconstructionListFichierNew() {
        ListFichierNew.getSelectionModel().clearSelection();
        popFichierNew= FXCollections.observableArrayList(popFichierNewOri);
        ListFichierNew.setItems(popFichierNew);
        ListFichierNew.refresh();
    }
    private void reconstructionListRepertoirePhoto() {
        ListRepertoirePhoto.getSelectionModel().clearSelection();
        popRepertoirePhoto= FXCollections.observableArrayList(popRepertoirePhotoOri);
        ListRepertoirePhoto.setItems(popRepertoirePhoto);
        ListRepertoirePhoto.refresh();
    }


    //TODO
    @FXML
    void transfertFile() {
//
//        int numeroFileSelect = ListFichierNew.getSelectionModel().getSelectedIndex();
//        int numeroRepertorySelect = ListRepertoirePhoto.getSelectionModel().getSelectedIndex();
//
//        String source = popRepNew.get(numeroFileSelect);
//        Path p = Paths.get(source);
//        String file = p.getFileName().toString();
//        GroupeDePhoto groupeDePhotoDest = popRepertoirePhoto.get(numeroRepertorySelect);
//        String dest = groupeDePhotoDest.getPath() + File.separator + file ;
//        p = null;
//
//        try {
//
//
////            Path temp = Files.move
////                    (Paths.get(source),
////                            Paths.get(dest));
//
//            FileChannel inputChannel = null;
//            FileChannel outputChannel = null;
//            try {
//                inputChannel = new FileInputStream(source).getChannel();
//                outputChannel = new FileOutputStream(dest).getChannel();
//
//                outputChannel.transferFrom(inputChannel, 0, inputChannel.size());
//            } finally {
//                inputChannel.close();
//                outputChannel.close();
//            }
//
//            groupeDePhotoDest.addfile(dest);
//            ListRepertoirePhoto.refresh();
//
//            //select next
//            popRepNew.remove(numeroFileSelect);
//            ListFichierNew.refresh();
//
//            if (numeroFileSelect<popRepNew.size()) {
//                ListFichierNew.getSelectionModel().select(numeroFileSelect);
//            } else {
//                ListFichierNew.getSelectionModel().select(popRepNew.size());
//            }
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    private void isTransfertPossible() {
        BouttonTransfertFile.setDisable(true);
        if(ListRepertoirePhoto.getSelectionModel().getSelectedItems().size() == 1) {
            if (ListFichierNew.getSelectionModel().getSelectedItems().size() == 1){
                BouttonTransfertFile.setDisable(false);
            } else {
                if ((ListFichierNew.getSelectionModel().getSelectedItems().size() >= 1)
                        && (!MasterSelectionNew)) {
                    BouttonTransfertFile.setDisable(false);
                }
            }
        }
    }
}
