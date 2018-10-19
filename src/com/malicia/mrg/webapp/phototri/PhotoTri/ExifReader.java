package com.malicia.mrg.webapp.phototri.PhotoTri;
import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;
import com.drew.metadata.exif.ExifDirectoryBase;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;

public class ExifReader
{

    /**
     * 读取照片里面的信息
     */
    public static String printImageTags(String filename){
        File file = new File( filename );
        Metadata metadata = null;
        try {
            metadata = ImageMetadataReader.readMetadata(file);
        String createDate = null;
        String lat = null;
        String lon = null;
        for (Directory directory : metadata.getDirectories()) {
            for (Tag tag : directory.getTags()) {
                String tagName = tag.getTagName();
                String desc = tag.getDescription();
                switch (tagName) {
                    case "Date/Time Original":
                        return desc.split(" ")[0].replace(":", "");
                    case "GPS Latitude":
                        lat = desc;
                        break;
                    case "GPS Longitude":
                        lon = desc;
                        break;
                }
            }
        }
        } catch (ImageProcessingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    public ExifReader(String[] args) {

 //   }
 //   public static void main(String[] args ){
        if( args.length == 0 )
        {
            System.out.println( "Usage: Test <image-file>" );
            System.exit( 0 );
        }

        String filename = args[ 0 ];
        System.out.println( "Filename: " + filename );

        try
        {
            File jpgFile = new File( filename );
            Metadata metadata = ImageMetadataReader.readMetadata( jpgFile );

            // Read Exif Data
            Directory directory = metadata.getFirstDirectoryOfType( ExifDirectoryBase.class );
            if( directory != null )
            {
                // Read the date
                Date date = directory.getDate( ExifDirectoryBase.TAG_DATETIME );
                DateFormat df = DateFormat.getDateInstance();
                df.format( date );
                int year = df.getCalendar().get( Calendar.YEAR );
                int month = df.getCalendar().get( Calendar.MONTH ) + 1;

                System.out.println( "Year: " + year + ", Month: " + month );

                System.out.println( "Date: " + date );

                System.out.println( "Tags" );
                for(Iterator i = directory.getTags().iterator() ; i.hasNext(); )
                {
                    Tag tag = ( Tag )i.next();
                    System.out.println( "\t" + tag.getTagName() + " = " + tag.getDescription() );

                }
            }
            else
            {
                System.out.println( "EXIF is null" );
            }

        }
        catch( Exception e )
        {
            e.printStackTrace();
        }

    }
}