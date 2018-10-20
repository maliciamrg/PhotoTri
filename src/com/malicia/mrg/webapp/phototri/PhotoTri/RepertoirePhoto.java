package com.malicia.mrg.webapp.phototri.PhotoTri;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class RepertoirePhoto
{

	public int getNbFichier() {
		return nbFichier;
	}

	int nbFichier;
	String path;
	String name;
	String dateDebyyyymmjj;
	String dateFinyyyymmjj;

	public RepertoirePhoto(String pathin)
	{
		this.path = pathin;
		String partpath=path;
		this.name=partpath;
		this.nbFichier =0;
		this.dateDebyyyymmjj="99991231";
		this.dateFinyyyymmjj="00010101";

		//boucle fichier du repertoire
		ObservableList data = FXCollections.observableArrayList();
		try (Stream<Path> paths = Files.walk(Paths.get(this.path))) {
			paths
					.filter(path -> Files.isRegularFile(path))
					.filter(p -> Model.filterSelectionFile(p))
					.distinct()
					.forEach(x -> this.addfile(x.toString()));
		} catch (IOException e) {
			e.printStackTrace();
		}
		//
	}

	public Boolean isElegible(String p0)
	{
		if (p0.compareTo(dateDebyyyymmjj)>0 && p0.compareTo(dateFinyyyymmjj)<0 ){
			return true;
		}
		return false;
	}

	public void addfile(String p0)
	{
		nbFichier +=1;
		String p0date = this.getdatefromfile(p0);
		setDateDebyyyymmjj(p0date);
		setDateFinyyyymmjj(p0date);
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
		if (dateDebyyyymmjjin.compareTo(dateDebyyyymmjj)<0 ){
		this.dateDebyyyymmjj = dateDebyyyymmjjin;
		}
	}

	
	private void setDateFinyyyymmjj(String dateFinyyyymmjjin)
	{
		if (dateFinyyyymmjjin.compareTo(dateFinyyyymmjj)>0 ){
			this.dateFinyyyymmjj=dateFinyyyymmjjin;
		}
	}


    @Override
    public String toString() {
        return "RepertoirePhoto{" +
                "nbFichier=" + nbFichier +
                ", dateDebyyyymmjj='" + dateDebyyyymmjj + '\'' +
                ", dateFinyyyymmjj='" + dateFinyyyymmjj + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
