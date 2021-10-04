// +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=
// Support.java
// Period-Countdown
//
// Created by Jonathan Uhler on 9/28/21
// +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=


package graphics;


import calendar.SchoolCalendar;
import main.PeriodCountdown;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import updater.Download;


public class Support {

    private void versionAction() {
        ArrayList<Object> message = new ArrayList<>();
        message.add("Version " + PeriodCountdown.VERSION);

        JOptionPane.showConfirmDialog(
                null,
                message.toArray(new Object[0]),
                "Version",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.PLAIN_MESSAGE,
                null);
    }


    private JMenuItem getVersion() {
        JMenuItem versionMenu = new JMenuItem("Version");
        versionMenu.addActionListener(e -> {
            try { this.versionAction(); } catch (Exception ex) { ex.printStackTrace(); }
        });
        return versionMenu;
    }


    private JMenuItem getIssue() {
        JMenuItem issueMenu = new JMenuItem("Submit an Issue");
        issueMenu.addActionListener(e -> {
            try {Desktop.getDesktop().browse(java.net.URI.create("https://github.com/JonathanUhler/Period-Countdown/issues/new"));} catch (IOException ex) {ex.printStackTrace();}
        });
        return issueMenu;
    }


    private void updateJsonAction() throws Exception {
        Download.download("https://raw.githubusercontent.com/JonathanUhler/Period-Countdown/main/src/json/MVHS_School.json");

        Path source = Paths.get(new File(new File(new File(SchoolDisplay.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getPath()).getAbsolutePath()).getParent() +
                SchoolCalendar.FILE_SEP + "MVHS_School.json");
        Path dest = Paths.get(SchoolDisplay.periodCountdownDirectory + SchoolCalendar.FILE_SEP + "json" + SchoolCalendar.FILE_SEP + SchoolDisplay.defaultSchoolData);

        Files.delete(dest);
        Files.move(source, dest);
    }


    private void updateJarAction() throws Exception {
        Download.download("https://raw.githubusercontent.com/JonathanUhler/Period-Countdown/main/src/PeriodCountdown.jar");
    }


    private void updateAction() {
        ArrayList<Object> message = new ArrayList<>();
        message.add("Current Version " + PeriodCountdown.VERSION +
                "\nUpdating this software will pull the latest changes from https://github.com/JonathanUhler/Period-Countdown/ to your local machine" +
                "\nIf you do not consent to pulling remote files to your machine, press \"Cancel\"");

        int confirmUpdate = JOptionPane.showConfirmDialog(
                null,
                message.toArray(new Object[0]),
                "Confirm Update",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE,
                null);

        if (confirmUpdate == JOptionPane.OK_OPTION) {
            try {this.updateJsonAction();} catch (Exception ex) {ex.printStackTrace();}
            try {this.updateJarAction();} catch (Exception ex) {ex.printStackTrace();}

            JOptionPane.showConfirmDialog(
                    null,
                    "Latest branch from Github has successfully been pulled.\nA restart of this software is needed for changes to take effect.",
                    "Update Success",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.PLAIN_MESSAGE,
                    null);
        }
    }


    private JMenuItem getUpdate() {
        JMenuItem updateMenu = new JMenuItem("Update");

        updateMenu.addActionListener(e -> {
            this.updateAction();
        });

        return updateMenu;
    }


    public JMenu getSupportMenu() {
        JMenu supportMenu = new JMenu("Support");

        supportMenu.add(this.getVersion());
        supportMenu.add(this.getUpdate());
        supportMenu.add(this.getIssue());

        return supportMenu;
    }

}
