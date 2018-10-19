package com.malicia.mrg.webapp.phototri.PhotoTri;
public class RepertoirePhoto
{

	String path;
	String name;
	String dateDebyyyymmjj;
	String dateFinyyyymmjj;

	public RepertoirePhoto(String pathin)
	{
		this.path = pathin;
		String partpath=path;
		this.name=partpath;
this.dateDebyyyymmjj="99991231";
this.dateFinyyyymmjj="00010101";
//boucle fichier du repertoir
		String fichierrepertoire = "pathfile";
		this.addfile(fichierrepertoire);
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
		String p0date = this.getdatefromfile(p0);
		setDateDebyyyymmjj(p0date);
		setDateFinyyyymmjj(p0date);
	}

	private String getdatefromfile(String p0)
{
		return "20180801";
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

	
}
