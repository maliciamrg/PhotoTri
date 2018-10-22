package com.malicia.mrg.photo.object.groupphoto;

import com.malicia.mrg.photo.exifreader.ExifReader;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.List;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class GroupeDePhoto
{

	public int getNbFichier() {
		return nbFichier;
	}

	int nbFichier;

	public String getPath() {
		return path;
	}

	String path;
	String name;
	String dateDebyyyymmjj;
	String dateFinyyyymmjj;

	String keyOrder;
    ObservableList<String> listFiles;

	public GroupeDePhoto( String pathin)
	{
        this.path = pathin;
        String partpath=path;

        String[] nameSplit = partpath.split(Pattern.quote("\\"));
        String nameOne = nameSplit[nameSplit.length - 1];
        if (nameOne.toLowerCase().compareTo("rejet")==0) {
            nameOne = nameSplit[nameSplit.length - 2];
        }
        this.name=nameOne;
        initialize();
        //boucle fichier du repertoire
        ObservableList data = FXCollections.observableArrayList();
        try (Stream<Path> paths = Files.walk(Paths.get(this.path),1)) {
            paths
                    .filter(path -> Files.isRegularFile(path))
                    .filter(p -> filterSelectionFile(p))
                    .distinct()
                    .forEach(x -> this.addfile(x.toString()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        //
    }
	public GroupeDePhoto()
        {
            this.name="mixed";
            initialize();
        }

    private void initialize() {
        this.nbFichier =0;
        this.dateDebyyyymmjj="20991231";
        this.dateFinyyyymmjj="00010101";
        this.listFiles = FXCollections.observableArrayList();
    }

    public Boolean isElegible(String p0)
	{
		if (p0.compareTo(dateDebyyyymmjj)>=0 && p0.compareTo(dateFinyyyymmjj)<=0 ){
			return true;
		}
		return false;
	}

	public void addfile(String fileName)
	{
		nbFichier +=1;
		String p0date = this.getdatefromfile(fileName);
		setDateDebyyyymmjj(p0date);
		setDateFinyyyymmjj(p0date);
        listFiles.add(fileName);
	}

	private String getdatefromfile(String fichier)
	{
		return ExifReader.printImageTags (fichier);
	}

	public String getName()
	{
		return name;
	}

	private void setDateDebyyyymmjj(String dateDebyyyymmjjin)
	{
		if (dateDebyyyymmjjin.compareTo(dateDebyyyymmjj)<0 && dateDebyyyymmjjin.compareTo("")>0){
		this.dateDebyyyymmjj = dateDebyyyymmjjin;
		}
	}

	
	private void setDateFinyyyymmjj(String dateFinyyyymmjjin)
	{
		if (dateFinyyyymmjjin.compareTo(dateFinyyyymmjj)>0 && dateFinyyyymmjjin.compareTo("")>0){
			this.dateFinyyyymmjj=dateFinyyyymmjjin;
		}
	}


    @Override
    public String toString() {
        return "" +
                "" + dateDebyyyymmjj +
                "" + dateFinyyyymmjj +
                " (" + String.format("%04d", nbFichier) + ")" +
                " " + name ;
    }

    public String toStringInfo() {
        return "GroupeDePhoto{" +
                "nbFichier=" + nbFichier +
                ", dateDebyyyymmjj='" + dateDebyyyymmjj + '\'' +
                ", dateFinyyyymmjj='" + dateFinyyyymmjj + '\'' +
                ", name='" + name + '\'' +
                '}';
    }

    private static boolean filterSelectionFile(Path p) {
        String fileLow = p.toString().toLowerCase();
        return fileLow.endsWith(".jpg") || fileLow.endsWith(".jpeg") || fileLow.endsWith(".png") || fileLow.endsWith(".mp4") || fileLow.endsWith(".arw");
    }

    public ObservableList<String> getListFiles() {
        return listFiles;
    }

	public String getKeyOrder() {
		return keyOrder;
	}


    public static int compare(GroupeDePhoto a, GroupeDePhoto b)
    {
        return (a.dateDebyyyymmjj + a.dateFinyyyymmjj).compareTo((b.dateDebyyyymmjj + b.dateFinyyyymmjj));
    }


}
