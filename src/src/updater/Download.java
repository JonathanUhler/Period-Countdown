// +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=
// Download.java
// Period-Countdown
//
// Created by Jonathan Uhler on 10/2/21
// +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=


// +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=
// Download.java
// Class Diagram
/*

+-----------------------------------------------------------------------------------------------------+
|                                               Download                                              |
+-----------------------------------------------------------------------------------------------------+
| -getFileName(String): String                                                                        |
| +download(String): void                                                                             |
+-----------------------------------------------------------------------------------------------------+

*/
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
        String[] urlSplit = downloadURL.split("/"); // Split the URL by the / delimiter
        String name = urlSplit[urlSplit.length - 1]; // Get the name of the file as the last element of the split

        // Check that the name of the file is not nothing
        if (name.equals("")) {
            return null;
        }

        // Handle questions mark delimiters in the file path
        int questionMarkIndex = name.indexOf("?");
        if (questionMarkIndex != -1) {
            name = name.substring(0, questionMarkIndex);
        }

        // Replace any hyphens in the file name and return the final name
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
    // Returns--
    //
    // None
    //
    public static void download(String downloadURL) throws Exception {
        URL website = new URL(downloadURL); // Get the website from the download url
        String fileName = getFileName(downloadURL); // Get the name of the file to download
        // Specify the absolute path to put the downloaded file --> Get the current directory of the running code and put the file there
        String filePath = new File(new File(new File(SchoolDisplay.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getPath()).getAbsolutePath()).getParent() + SchoolCalendar.FILE_SEP + fileName;

        // Download the file
        try (InputStream inputStream = website.openStream()) {
            assert fileName != null;
            // Copy from the input filestream into the new file
            // Replace the file if it already exists --> force the download
            Files.copy(inputStream, Paths.get(filePath), StandardCopyOption.REPLACE_EXISTING);
        }
    }
    // end: public static void download

}
// end: public class Download