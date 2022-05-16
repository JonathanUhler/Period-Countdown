// +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=
// Download.java
// Period-Countdown
//
// Created by Jonathan Uhler on 10/2/21
// +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=


package updater;


import calendar.SchoolCalendar;
import graphics.SchoolDisplay;
import java.io.File;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;


// +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=
// public class Download
//
// Handles downloading files from the internet
//
public class Download {

    // ====================================================================================================
    // private static String getFileName
    //
    // Gets the name of a file given a download URL
    //
    // Arguments--
    //
    // downloadURL: the url of the file to be downloaded that the name should be found for
    //
    // Returns--
    //
    // The name + extension of the file
    //
    private static String getFileName(String downloadURL) throws UnsupportedEncodingException {
        String[] urlSplit = downloadURL.split("/");
        String name = urlSplit[urlSplit.length - 1];

        // Return the name of the file based on some possible conditions
        //  If the name is nothing (an empty string) return null
        //  If the name as a question mark (as some URLs might) then get everything before the question mark
        //  Finally remove any hyphens in the file name to make sure it is a valid string/name for all
        //  conditions and return the file name
        if (name.equals(""))
            return null;

        int questionMarkIndex = name.indexOf("?");
        if (questionMarkIndex != -1)
            name = name.substring(0, questionMarkIndex);

        name = name.replaceAll("-", "");
        return URLDecoder.decode(name, "UTF-8");
    }
    // end: private static String getFileName


    // ====================================================================================================
    // public static void download
    //
    // Download a file from a given URL on the internet
    //
    // Arguments--
    //
    // downloadURL: the url of the file to download
    //
    public static void download(String downloadURL) throws Exception {
        // Prepare for the download by getting the website to download from and the directory path to
        // place the downloaded file
        URL website = new URL(downloadURL);

        String fileName = getFileName(downloadURL);
        String codePath = new File(SchoolDisplay.class.getProtectionDomain()
                .getCodeSource()
                .getLocation()
                .toURI()).getPath();
        String codePathParent = new File(new File(codePath).getAbsolutePath()).getParent();
        String filePath = codePathParent + SchoolCalendar.FILE_SEP + fileName;

        // Copy the contents of the file from the internet into the new or existing file at filePath. Force
        // the download to complete by replacing the file if it exists
        try (InputStream inputStream = website.openStream()) {
            assert fileName != null;
            Files.copy(inputStream, Paths.get(filePath), StandardCopyOption.REPLACE_EXISTING);
        }
    }
    // end: public static void download

}
// end: public class Download