package com.malicia.mrg.photo.app.phototri;

import com.malicia.mrg.photo.object.groupphoto.GroupeDePhoto;
import com.malicia.mrg.sqlite.SQLiteJDBCDriverConnection;
import com.malicia.mrg.sqlite.ShowResultsetInJtable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.io.File;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;


public class Model extends Window {


    public String chooseFileLrcat(){
        FileChooser fileChooser = new FileChooser ();
        fileChooser.setInitialDirectory(new File("d:/"));
        fileChooser.setTitle("Choose file Lrcat");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Lrcat", "*.lrcat")
        );
        File selectedFile = fileChooser.showOpenDialog(this);
        return selectedFile.getAbsolutePath() ;
    }

//    public ObservableList<String> populateFile( String repertoire) {
//        grpPhotoNew = new GroupeDePhoto();
//        try (Stream<Path> paths = Files.walk(Paths.get(repertoire),1)) {
//            paths
//                    .filter(path -> Files.isRegularFile(path))
//                    .distinct()
//                    .forEach((x -> grpPhotoNew.addfile(x.toString())));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return grpPhotoNew.getListFiles();
//    }


    public ObservableList<GroupeDePhoto> populateRepertoirePhotoBySqllite(String databaseFile) {

        String RepertoireNew = Main.properties.getProperty("RepertoireNew");
        String RepertoirePhoto = Main.properties.getProperty("RepertoirePhoto");
        String TempsAdherenceM = Main.properties.getProperty("TempsAdherenceM");
        String TempsAdherenceP = Main.properties.getProperty("TempsAdherenceP");
        String GroupingTime = Main.properties.getProperty("GroupingTime");

        ObservableList<GroupeDePhoto> grpListPhotoRepertoire = FXCollections.observableArrayList();
        SimpleDateFormat formattertodate = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat formattertoyymmdd = new SimpleDateFormat("yyyyMMdd");
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
        SQLiteJDBCDriverConnection sql = new SQLiteJDBCDriverConnection();
        sql.connect(databaseFile);
//        sql.select("select  min(e.captureTime) , max(e.captureTime) , c.absolutePath , b.pathFromRoot , count(*) " +
//                "from AgLibraryFile a " +
//                "inner join AgLibraryFolder b " +
//                "on a.folder = b.id_local " +
//                "inner join AgLibraryRootFolder c " +
//                "on b.rootFolder = c.id_local " +
//                "inner join Adobe_images e " +
//                "on a.id_local = e.rootFile " +
//                "group by  c.absolutePath , b.pathFromRoot");

        sql.execute("DROP TABLE IF EXISTS Repertory;  ");

        sql.execute("DROP TABLE IF EXISTS NewPhoto;  " );

        sql.execute("DROP TABLE IF EXISTS GroupNewPhoto;  " );


        sql.execute("CREATE TEMPORARY TABLE Repertory AS  " +
                "select e.captureTime as ortime ,  strftime('%s', DATETIME( e.captureTime,"+TempsAdherenceM+")) as mint , strftime('%s', DATETIME(e.captureTime,"+TempsAdherenceP+")) as maxt , c.absolutePath , b.pathFromRoot   " +
                " from AgLibraryFile a  " +
                "inner join AgLibraryFolder b  " +
                "on a.folder = b.id_local  " +
                "inner join AgLibraryRootFolder c  " +
                "on b.rootFolder = c.id_local  " +
                "inner join Adobe_images e  " +
                "on a.id_local = e.rootFile  " +
                "Where  b.pathFromRoot like \"" + RepertoirePhoto + "%\" " +
                " ;");
//                "group by  c.absolutePath , b.pathFromRoot ;  " );

        sql.execute( "CREATE TEMPORARY TABLE NewPhoto AS  " +
                "select  strftime('%s', e.captureTime) as captureTime , c.absolutePath , b.pathFromRoot ,a.originalFilename   " +
                "from AgLibraryFile a  " +
                "inner join AgLibraryFolder b  " +
                "on a.folder = b.id_local  " +
                "inner join AgLibraryRootFolder c  " +
                "on b.rootFolder = c.id_local  " +
                "inner join Adobe_images e  " +
                "on a.id_local = e.rootFile  " +
                "Where b.pathFromRoot like \"%" + RepertoireNew + "%" + "\";  ");



//TODO a faire
        sql.execute( "CREATE TEMPORARY TABLE GroupNewPhoto AS  " +
                "select  strftime('%s', e.captureTime) as captureTime , c.absolutePath , b.pathFromRoot ,a.originalFilename   " +
                "from AgLibraryFile a  " +
                "inner join AgLibraryFolder b  " +
                "on a.folder = b.id_local  " +
                "inner join AgLibraryRootFolder c  " +
                "on b.rootFolder = c.id_local  " +
                "inner join Adobe_images e  " +
                "on a.id_local = e.rootFile  " +
                "Where b.pathFromRoot like \"%" + RepertoireNew + "%" + "\";  ");





        sql.select("SELECT distinct  " +
//              " a.ortime, a.mint, b.captureTime, a.maxt ," +
//              " a.ortime, a.mint, b.captureTime, a.maxt ," +
                " a.absolutePath , b.pathFromRoot ,b.originalFilename , a.pathFromRoot  FROM Repertory a  " +
//        ";");
                "inner join NewPhoto b  " +
                "on b.captureTime between a.mint and a.maxt;"  +
                "order by  a.absolutePath , a.pathFromRoot ;");
        try {

            new ShowResultsetInJtable(sql,"auto match extract Ligthroom","@new->!") .invoke();

            while (sql.rs.next()) {

                String x = sql.rs.getString(1) + sql.rs.getString(4);
                String pathString = x.toLowerCase();
                if (!pathString.contains(RepertoireNew.toLowerCase())
                && pathString.contains(RepertoirePhoto.toLowerCase())
                ) {
                    try {
                        String dateDeb = formattertoyymmdd.format(formattertodate.parse(sql.rs.getString(1)));
                        String dateFin = formattertoyymmdd.format(formattertodate.parse(sql.rs.getString(2)));
                        GroupeDePhoto dePhoto = new GroupeDePhoto(x.toString(), dateDeb, dateFin,sql.rs.getInt(5));
                        //       MessagePerso.addText(dePhoto.toStringInfo());
                        grpListPhotoRepertoire.add(dePhoto);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                };
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        java.util.Collections.sort(grpListPhotoRepertoire,(o1, o2) -> GroupeDePhoto.compare(o1,o2) );
        return grpListPhotoRepertoire;
    }

    
    public ObservableList<GroupeDePhoto> populateFichierPhotoBySqllite(String databaseFile) {

        String RepertoireNew = Main.properties.getProperty("RepertoireNew");

        ObservableList<GroupeDePhoto> grpListPhotoFichier = FXCollections.observableArrayList();
        SimpleDateFormat formattertodate = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat formattertoyymmdd = new SimpleDateFormat("yyyyMMdd");
//AgLibraryRootFolder
//AgLibraryFolder
//AgLibraryFile
//Adobe_images
//Adobe_imageProperties
//
//select  e.captureTime , e.captureTime , c.absolutePath , b.pathFromRoot ,a.originalFilename
//from AgLibraryFile a
//inner join AgLibraryFolder b
//on a.folder = b.id_local
//inner join AgLibraryRootFolder c
//on b.rootFolder = c.id_local
//inner join Adobe_images e
//on a.id_local = e.rootFile
//Where b.pathFromRoot like "@New%"
        SQLiteJDBCDriverConnection sql = new SQLiteJDBCDriverConnection();
        sql.connect(databaseFile);
        sql.select("select  e.captureTime , e.captureTime , c.absolutePath , b.pathFromRoot ,a.originalFilename " +
                "from AgLibraryFile a " +
                " inner join AgLibraryFolder b " +
                "  on a.folder = b.id_local " +
                " inner join AgLibraryRootFolder c " +
                "  on b.rootFolder = c.id_local " +
                " inner join Adobe_images e " +
                "  on a.id_local = e.rootFile " +
                " Where b.pathFromRoot like \"" + RepertoireNew + "%" + "\"");
        try {
            while (sql.rs.next()) {

                String x = sql.rs.getString(3) + sql.rs.getString(4) + sql.rs.getString(5);

                File f = new File(x);
                if(f.exists()) {
                    try {
                        String dateDeb;
                        String dateFin;
                        if (sql.rs.getString(1) == null) {
                            dateDeb = "00010101";
                            dateFin = "20991231";
                        } else {
                            dateDeb = formattertoyymmdd.format(formattertodate.parse(sql.rs.getString(1)));
                            dateFin = formattertoyymmdd.format(formattertodate.parse(sql.rs.getString(2)));
                        }
                        GroupeDePhoto dePhoto = new GroupeDePhoto(x, dateDeb, dateFin, 1);
                        //       MessagePerso.addText(dePhoto.toStringInfo());
                        grpListPhotoFichier.add(dePhoto);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        java.util.Collections.sort(grpListPhotoFichier,(o1, o2) -> GroupeDePhoto.compare(o1,o2) );
        return grpListPhotoFichier;
    }



//    public ObservableList<GroupeDePhoto> populateRepertory(String repertoire) {
//
//        //       MessagePerso.makeText(Main.getPrimaryStage(),"populateRepertory") ;
//        ObservableList<GroupeDePhoto> grpListPhotoRepertoire = FXCollections.observableArrayList();
//        try (Stream<Path> paths = Files.walk(Paths.get(repertoire))) {
//            paths
//                    .filter( Files::isDirectory)
//
//                    .distinct()
//                    .forEach((x -> isAdd(grpListPhotoRepertoire, x)));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        java.util.Collections.sort(grpListPhotoRepertoire,(o1, o2) -> GroupeDePhoto.compare(o1,o2) );
// //       MessagePerso.killText();
//        return grpListPhotoRepertoire;
//    }
//
//    private void isAdd(ObservableList<GroupeDePhoto> grpListPhotoRepertoire, Path x) {
//
//        String pathString = x.toString().toLowerCase();
//        if (!pathString.contains("rejet") &&
//            !pathString.contains("@") &&
//            !pathString.contains("&") &&
//            !pathString.contains("!!") ) {
//            GroupeDePhoto dePhoto = new GroupeDePhoto(x.toString());
//            //       MessagePerso.addText(dePhoto.toStringInfo());
//            grpListPhotoRepertoire.add(dePhoto);
//        };
//    }


//    private void alertinfo(String title,String header,String content) {
//        Alert alert = new Alert(Alert.AlertType.INFORMATION);
//        alert.setTitle(title);
//        alert.setHeaderText(header);
//        alert.setContentText(content);
//        alert.showAndWait();
//    }


}
