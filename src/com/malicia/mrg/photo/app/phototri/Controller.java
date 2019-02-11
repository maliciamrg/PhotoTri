package com.malicia.mrg.photo.app.phototri;

import com.malicia.mrg.photo.object.groupphoto.GroupeDePhoto;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.awt.*;
import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

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
    @FXML
    private ProgressBar progressBar;

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
    void repertoireKeyPressed(KeyEvent event) {
//        if (event.getCode() == KeyCode.F) {
//
//        }
    }

    @FXML
    void fichierKeyPressed(KeyEvent event) {
        if (event.getCode() == KeyCode.DELETE){

            ObservableList<Integer> itemFileSelect = ListFichierNew.getSelectionModel().getSelectedIndices();

            int max = itemFileSelect.size()-1;
            Integer numeroFileSelect;
            String[] source = new  String[max+1];

            for (int i = 0; i <= max; i++) {
                numeroFileSelect = itemFileSelect.get(i);
                source[i] = popFichierNew.get(numeroFileSelect).getPath();
                System.out.println(source[i]);

            }

            ListFichierNew.cellFactoryProperty();
            ListFichierNew.refresh();

            for (int i = 0; i <= max; i++) {
                System.out.println(source[i]);

                try {
                    Files.delete(Paths.get(source[i]));
                    System.out.println("----delete Ok---");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            populateListFichierNew();
        }
//        if (event.getCode() == KeyCode.N && event.isControlDown()){
//
//            ObservableList<Integer> itemFileSelect = ListFichierNew.getSelectionModel().getSelectedIndices();
//
//            int max = itemFileSelect.size()-1;
//            Integer numeroFileSelect;
//            String[] source = new  String[max+1];
//
//            for (int i = 0; i <= max; i++) {
//                numeroFileSelect = itemFileSelect.get(i);
//                source[i] = popFichierNew.get(numeroFileSelect).getPath();
//                System.out.println(source[i]);
//
//            }
//
//            ListFichierNew.cellFactoryProperty();
//            ListFichierNew.refresh();
//
//            for (int i = 0; i <= max; i++) {
//                System.out.println(source[i]);
//
//                try {
//                    Files.move(Paths.get(source[i]));
//                    System.out.println("----delete Ok---");
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//
//            populateListFichierNew();
//        }
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
        String CatalogLrcat = Main.properties.getProperty("CatalogLrcat");
        TextDatabaseLigthroom.setText(CatalogLrcat);
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
                    File fileImage = new File(name.getPath());
                    Image imgTmp = new Image(fileImage.toURI().toString());
                    imageView.setImage(imgTmp);
                    imageView.setFitWidth(120);
                    imageView.setPreserveRatio(true);
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

            Integer listRepSelect = ListRepertoirePhoto.getSelectionModel().getSelectedIndex();

            changeMasterSelection();
            changeMasterSelection();

            ListRepertoirePhoto.getSelectionModel().selectIndices(listRepSelect);

            BouttonTransfertFile.setDisable(true);


            //runTask();

            progressBar.setProgress(0);

            GroupeDePhoto groupeDePhoto = ListRepertoirePhoto.getSelectionModel().getSelectedItem();

            int max = popFichierNew.size()-1;
            int nbSupp = 0;
            for (int i = 0; i <= max; i++) {
                System.out.println(" | " +String.valueOf(i)+" - "+String.valueOf(max)+" - "+String.valueOf(nbSupp)+" | ");
                double percent = i / max;
                progressBar.setProgress(percent);

                String fichierDateyyyymmjj = popFichierNew.get(i-nbSupp).getDateDebyyyymmjj();
                    if (groupeDePhoto.isElegible(fichierDateyyyymmjj)) {
                    ListFichierNew.getSelectionModel().select(i-nbSupp);
                } else {
                    popFichierNew.remove(i-nbSupp);
                    nbSupp +=1;
                }

            }


//            int i;
//            for (i = 0; i < popFichierNew.size(); i += 1) {
//
//
//
//                String fichierDateyyyymmjj = popFichierNew.get(i).getDateDebyyyymmjj();
//                if (groupeDePhoto.isElegible(fichierDateyyyymmjj)) {
//                    ListFichierNew.getSelectionModel().select(i);
//                } else {
//                    popFichierNew.remove(i);
//                    i-=1;
//                }
//            }

//            ListFichierNew.setItems(popFichierNew);
//            ListFichierNew.refresh();

        }

        isTransfertPossible();

    }

    private void reconstructionListFichierNew() {
//        ListFichierNew.getItems().clear();
        popFichierNew= FXCollections.observableArrayList(popFichierNewOri);
        ListFichierNew.setItems(popFichierNew);
        ListFichierNew.refresh();
        ListFichierNew.getSelectionModel().clearSelection();
    }
    private void reconstructionListRepertoirePhoto() {
        ListRepertoirePhoto.getSelectionModel().clearSelection();
        popRepertoirePhoto= FXCollections.observableArrayList(popRepertoirePhotoOri);
        ListRepertoirePhoto.setItems(popRepertoirePhoto);
        ListRepertoirePhoto.refresh();
    }



    @FXML
    void transfertFile() {

        ObservableList<Integer> itemFileSelect = ListFichierNew.getSelectionModel().getSelectedIndices();

        int max = itemFileSelect.size()-1;
        Integer numeroFileSelect;

        String[] source = new  String[max+1];
        String[] dest = new  String[max+1];

        for (int i = 0; i <= max; i++) {

            numeroFileSelect = itemFileSelect.get(i);


            source[i] = popFichierNew.get(numeroFileSelect).getPath();

            int numeroRepertorySelect = ListRepertoirePhoto.getSelectionModel().getSelectedIndex();
            Path p = Paths.get(source[i]);
            String file = p.getFileName().toString();
            GroupeDePhoto groupeDePhotoDest = popRepertoirePhoto.get(numeroRepertorySelect);
            dest[i] = groupeDePhotoDest.getPath() + File.separator + file;
            p = null;

            System.out.println(source[i]);
            System.out.println(dest[i]);
        }


//                FileChannel inputChannel = null;
//                FileChannel outputChannel = null;
//                try {
//                    inputChannel = new FileInputStream(source).getChannel();
//                    outputChannel = new FileOutputStream(dest).getChannel();
//
//                    outputChannel.transferFrom(inputChannel, 0, inputChannel.size());
//                } finally {
//                    inputChannel.close();
//                    outputChannel.close();
//                }

        ListFichierNew.cellFactoryProperty();

        ListFichierNew.refresh();

        for (int i = 0; i <= max; i++) {
            System.out.println(source[i]);
            System.out.println(dest[i]);

            try {
                Files.move(Paths.get(source[i]), Paths.get(dest[i]), StandardCopyOption.REPLACE_EXISTING);
                System.out.println("----move Ok---");
            } catch (IOException e) {
                e.printStackTrace();
            }
//            File filesrc = new File(source);
//            boolean ret = filesrc.renameTo(new File(dest));
//            System.out.println("renameTo="+ret);
        }

        populateListFichierNew();

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
//
//    private void runTask() {
//
//        final double wndwWidth = 300.0d;
//        Label updateLabel = new Label("Running tasks...");
//        updateLabel.setPrefWidth(wndwWidth);
//        ProgressBar progress = new ProgressBar();
//        progress.setPrefWidth(wndwWidth);
//
//        VBox updatePane = new VBox();
//        updatePane.setPadding(new Insets(10));
//        updatePane.setSpacing(5.0d);
//        updatePane.getChildren().addAll(updateLabel, progress);
//
//        Stage taskUpdateStage = new Stage(StageStyle.UTILITY);
//        taskUpdateStage.setScene(new Scene(updatePane));
//        taskUpdateStage.show();
//
//        Task longTask = new Task<Void>() {
//            @Override
//            protected Void call() throws Exception {
//
//
//                GroupeDePhoto groupeDePhoto = ListRepertoirePhoto.getSelectionModel().getSelectedItem();
//
//                int max = popFichierNew.size();
//                for (int i = 0; i <= max; i++) {
//                    if (isCancelled()) {
//                        break;
//                    }
//                    updateProgress(i, max);
//                    updateMessage("Task part " + String.valueOf(i) + "/" + String.valueOf(max)  + " complete");
//
//                    String fichierDateyyyymmjj = popFichierNew.get(i).getDateDebyyyymmjj();
//                    if (groupeDePhoto.isElegible(fichierDateyyyymmjj)) {
//                        ListFichierNew.getSelectionModel().select(i);
//                    } else {
//                        popFichierNew.remove(i);
//                        i-=1;
//                        max -=1;
//                    }
//
//                }
//                return null;
//
//
//
//            }
//        };
//
//        longTask.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
//            @Override
//            public void handle(WorkerStateEvent t) {
//                taskUpdateStage.hide();
//            }
//        });
//
//
//        progress.progressProperty().bind(longTask.progressProperty());
//        updateLabel.textProperty().bind(longTask.messageProperty());
//
//        taskUpdateStage.show();
//        new Thread(longTask).start();
//
////        do {
//////            try {
//////                Thread.sleep(100);
//////            } catch (InterruptedException e) {
//////                e.printStackTrace();
//////            }
////        }while( !longTask.isRunning() );
//
//    }

}
