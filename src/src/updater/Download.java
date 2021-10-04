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


public class Download {

    private static String getFileName(String downloadURL) throws UnsupportedEncodingException {
        String[] urlSplit = downloadURL.split("/");
        String name = urlSplit[urlSplit.length - 1];

        if (name.equals("")) {
            return null;
        }

        int questionMarkIndex = name.indexOf("?");
        if (questionMarkIndex != -1) {
            name = name.substring(0, questionMarkIndex);
        }

        name = name.replaceAll("-", "");
        return URLDecoder.decode(name, "UTF-8");
    }


    public static void download(String downloadURL) throws Exception {
        URL website = new URL(downloadURL);
        String fileName = getFileName(downloadURL);
        String filePath = new File(new File(new File(SchoolDisplay.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getPath()).getAbsolutePath()).getParent() + SchoolCalendar.FILE_SEP + fileName;

        try (InputStream inputStream = website.openStream()) {
            assert fileName != null;
            Files.copy(inputStream, Paths.get(filePath), StandardCopyOption.REPLACE_EXISTING);
        }
    }

}
