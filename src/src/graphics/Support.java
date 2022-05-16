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
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import updater.Download;


// +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=
// public class Support
//
// Handles the "support" menu and update routine
//
public class Support {

    // ====================================================================================================
    // private void versionAction
    //
    // Popup to show the app version
    //
    private void versionAction() {
        JOptionPane.showConfirmDialog(
                null,
                "Version " + PeriodCountdown.VERSION,
                "Version",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.PLAIN_MESSAGE,
                null);
    }
    // end: private void versionAction


    // ====================================================================================================
    // private JMenuItem getVersion
    //
    // Initializes and returns the menu item object for the version
    //
    // Returns--
    //
    // versionMenu: the version menu item object
    //
    private JMenuItem getVersion() {
        JMenuItem versionMenu = new JMenuItem("Version");

        versionMenu.addActionListener(e -> {
            try { this.versionAction(); }
            catch (Exception ex) { ex.printStackTrace(); }
        });

        return versionMenu;
    }
    // end: private JMenuItem getVersion


    // ====================================================================================================
    // private JMenuItem getIssue
    //
    // Initializes and returns the menu item object for the Github issue hyperlink
    //
    // Returns--
    //
    // issueMenu:   the issue hyperlink JMenuItem object
    //
    private JMenuItem getIssue() {
        JMenuItem issueMenu = new JMenuItem("Submit an Issue");

        issueMenu.addActionListener(e -> {
            String githubIssueLink = "https://github.com/JonathanUhler/Period-Countdown/issues/new";
            try { Desktop.getDesktop().browse(URI.create(githubIssueLink)); }
            catch (IOException ex) { ex.printStackTrace(); }
        });

        return issueMenu;
    }
    // end: private JMenuItem getIssue


    // ====================================================================================================
    // private void updateJsonAction
    //
    // Downloads and updates the JSON school data
    //
    private void updateJsonAction() throws Exception {
        // To complete the update routine, begin by downloading the new json file from the github page.
        // Determine the source path where the file was downloaded to (this is 1 level up from the
        // path of the code being run). Finally, move the downloaded json file to the correct location
        Download.download("https://raw.githubusercontent.com/JonathanUhler/Period-Countdown/main/src/json/MVHS_School.json");

        String codePath = new File(SchoolDisplay.class.getProtectionDomain()
                .getCodeSource()
                .getLocation()
                .toURI()).getPath();
        String codePathParent = new File(new File(codePath).getAbsolutePath()).getParent();
        Path source = Paths.get( codePathParent + SchoolCalendar.FILE_SEP + "MVHS_School.json");
        Path dest = Paths.get(SchoolDisplay.periodCountdownDirectory +
                SchoolCalendar.FILE_SEP + "json" + SchoolCalendar.FILE_SEP + SchoolDisplay.defaultSchoolData);

        Files.delete(dest);
        Files.move(source, dest);
    }
    // end: private void updateJsonAction


    // ====================================================================================================
    // private void updateJarAction
    //
    // Downloads and updates the JAR file for Period-Countdown
    //
    private void updateJarAction() throws Exception {
        // Download the new JAR file
        // The new file will be in the same place as the current JAR and will replace it immediately, but a
        // // restart is needed for the changes to take effect
        Download.download("https://raw.githubusercontent.com/JonathanUhler/Period-Countdown/main/src/PeriodCountdown.jar");
    }
    // end: private void updateJarAction


    // ====================================================================================================
    // private void updateAction
    //
    // Updates the JSON school data and the JAR file
    //
    private void updateAction() {
        ArrayList<Object> message = new ArrayList<>();
        message.add("Current Version " + PeriodCountdown.VERSION +
                "\nUpdating this software will pull the latest changes from " +
                "https://github.com/JonathanUhler/Period-Countdown/ to your local machine" +
                "\nIf you do not consent to pulling remote files to your machine, press \"Cancel\"");
        int confirmUpdate = JOptionPane.showConfirmDialog(
                null,
                message.toArray(new Object[0]),
                "Confirm Update",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE,
                null);

        // After the user accepts a prompt warning them of how the update process works, continue with
        // the update by downloading the json and jar files from github. If either or both fail to
        // download, print an error message and some advice to the user. If both were downloaded
        // successfully, display a success message to the user.
        // Because a new jarfile is downloaded to replace the old one, the app must be relaunched
        // to start using the new jarfile, so the user is reminded of this if the download succeeds
        if (confirmUpdate == JOptionPane.OK_OPTION) {
            try {
                this.updateJsonAction();
                this.updateJarAction();
            }
            catch (Exception e) {
                JOptionPane.showConfirmDialog(
                        null,
                        "The update could not be completed\n\n" +
                                "Error: " + e.getMessage() + "\n\nTry:\n\t1) Updating using a privileged " +
                                "account\n\t2) Connecting to the internet",
                        "Update Failure",
                        JOptionPane.DEFAULT_OPTION,
                        JOptionPane.PLAIN_MESSAGE,
                        null);
                return;
            }

            JOptionPane.showConfirmDialog(
                    null,
                    "Latest branch from Github has successfully been pulled.\nA restart of this " +
                            "software is needed for changes to take effect.",
                    "Update Success",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.PLAIN_MESSAGE,
                    null);
        }
    }
    // end: private void updateAction


    // ====================================================================================================
    // private JMenuItem getUpdate
    //
    // Initialize and returns the menu item for the update button
    //
    // Returns--
    //
    // updateMenu:  the JMenuItem object for the update button
    //
    private JMenuItem getUpdate() {
        JMenuItem updateMenu = new JMenuItem("Update");
        updateMenu.addActionListener(e -> this.updateAction());
        return updateMenu;
    }


    // ====================================================================================================
    // public JMenu getSupportMenu
    //
    // Returns the "support" menu with the issues hyperlink, update routine, and other choices
    //
    // Returns--
    //
    // supportMenu: the JMenu for the support options
    //
    public JMenu getSupportMenu() {
        JMenu supportMenu = new JMenu("Support");

        supportMenu.add(this.getVersion());
        supportMenu.add(this.getUpdate());
        supportMenu.add(this.getIssue());

        return supportMenu;
    }
    // end: public JMenu getSupportMenu

}
// end: public class Support