package com.malicia.mrg.photo.app.phototri;

import com.malicia.mrg.photo.object.groupphoto.GroupeDePhoto;
import com.malicia.mrg.sqlite.SQLiteJDBCDriverConnection;
import com.malicia.mrg.sqlite.ShowResultsetInJtable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.io.File;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
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

        Statement stmt = null;

        String RepertoireNew = Main.properties.getProperty("RepertoireNew");
        String RepertoirePhoto = Main.properties.getProperty("RepertoirePhoto");
        String TempsAdherence = Main.properties.getProperty("TempsAdherence");

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

//
        sql.execute("CREATE TEMPORARY TABLE Repertory AS  " +
                "select e.captureTime as ortime ,  strftime('%s', DATETIME( e.captureTime,\"-"+TempsAdherence+"\")) as mint , strftime('%s', DATETIME(e.captureTime,\"+"+TempsAdherence+"\")) as maxt , c.absolutePath , b.pathFromRoot   " +
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
                "select  strftime('%s', e.captureTime) as captureTime , c.absolutePath , b.pathFromRoot ,a.originalFilename ,e.captureTime as captureTimeOrig  " +
                "from AgLibraryFile a  " +
                "inner join AgLibraryFolder b  " +
                "on a.folder = b.id_local  " +
                "inner join AgLibraryRootFolder c  " +
                "on b.rootFolder = c.id_local  " +
                "inner join Adobe_images e  " +
                "on a.id_local = e.rootFile  " +
                "Where b.pathFromRoot like \"%" + RepertoireNew + "%" + "\";  ");


//group photo
        sql.execute( "CREATE TEMPORARY TABLE GroupNewPhoto AS  " +
                "select a.* , '0' as numeroGroup  , strftime('%s', DATETIME( a.captureTimeOrig,\"+"+TempsAdherence+"\")) as captureTimeAdherence " +
                "from NewPhoto a  " +
                "order by a.capturetime ; ");

        try {
            stmt = SQLiteJDBCDriverConnection.conn.createStatement();
            ResultSet rsUpd = stmt.executeQuery(
               "SELECT distinct  " +
                    " * FROM GroupNewPhoto a  " +
                    ";");


            sql.select("SELECT distinct  " +
                    " * FROM GroupNewPhoto a  " +
                    ";");

            long captureTime =0;
            long captureTimeAdherence =  0;
            long numeroGroup = 0;
            long captureTimePrevious = 0;
            long captureTimeAdherencePrevious = 0;
            long numeroGroupPrevious = 0;
            while (rsUpd.next()) {

                captureTime = rsUpd.getLong("captureTime");
                captureTimeAdherence =rsUpd.getLong("captureTimeAdherence");
                //numeroGroup = rsUpd.getLong("numeroGroup");

                if (captureTimePrevious < captureTime && captureTime < captureTimeAdherencePrevious ){
                    numeroGroup = numeroGroupPrevious;
                } else {
                    numeroGroup = numeroGroupPrevious;
                    numeroGroup++;
                }

                boolean ret = sql.execute("UPDATE GroupNewPhoto  " +
                        " set numeroGroup = \"" + numeroGroup + "\"  " +
                        " where absolutePath = \"" + rsUpd.getString("absolutePath") + "\"  " +
                        "and pathFromRoot = \"" + rsUpd.getString("pathFromRoot") + "\"  " +
                        "and originalFilename = \"" + rsUpd.getString("originalFilename") + "\"  " +
                        " ");

//                rsUpd.updateString("numeroGroup",String.valueOf(numeroGroup));
///                rsUpd.updateRow();

                captureTimePrevious=captureTime;
                captureTimeAdherencePrevious= captureTimeAdherence;
                numeroGroupPrevious=numeroGroup;

            }

            sql.select("SELECT distinct  " +
                    " * FROM GroupNewPhoto a  " +
                    ";");

            new ShowResultsetInJtable( sql,"auto group @new","group @new") .invoke();

        } catch (SQLException e) {
            e.printStackTrace();
        }


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

    public String[] getColumnNameArray(ResultSet rs) {
        String sArr[] = null;
        try {
            ResultSetMetaData rm = rs.getMetaData();
            String sArray[] = new String[rm.getColumnCount()];
            for (int ctr = 1; ctr <= sArray.length; ctr++) {
                String s = rm.getColumnName(ctr);
                sArray[ctr - 1] = s;
            }
            return sArray;
        } catch (Exception e) {
            System.out.println(e);
            return sArr;
        }
    }
    public String[] getColumnTypeArray(ResultSet rs) {
        String sArr[] = null;
        try {
            ResultSetMetaData rm = rs.getMetaData();
            String sArray[] = new String[rm.getColumnCount()];
            for (int ctr = 1; ctr <= sArray.length; ctr++) {
                String s = rm.getColumnTypeName(ctr);
                sArray[ctr - 1] = s;
            }
            return sArray;
        } catch (Exception e) {
            System.out.println(e);
            return sArr;
        }
    }
    public int[] getType(ResultSet rs) {
        int iType[] = null;
        try {
            ResultSetMetaData rm = rs.getMetaData();
            int iArray[] = new int[rm.getColumnCount()];
            for (int ctr = 1; ctr <= iArray.length; ctr++) {
                int iVal = rm.getColumnType(ctr);
                iArray[ctr - 1] = iVal;
            }
            return iArray;
        } catch (Exception e) {
            System.out.println(e);
            return iType;
        }
    }
    public int getColumnCount(ResultSet rs) {
        int iOutput = 0;
        try {
            ResultSetMetaData rsMetaData = rs.getMetaData();
            iOutput = rsMetaData.getColumnCount();
        } catch (Exception e) {
            System.out.println(e);
            return iOutput = -1;
        }
        return iOutput;
    }
}
